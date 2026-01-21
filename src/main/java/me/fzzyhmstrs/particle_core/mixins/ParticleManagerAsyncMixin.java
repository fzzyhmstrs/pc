package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.SynchronizedIdentityHashMap;
import me.fzzyhmstrs.particle_core.interfaces.TickResult;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.crash.CrashException;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Restriction(
		require = {
				@Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
		}
)
@Mixin(value = ParticleManager.class, priority = 100000)
@Debug(export = true)
public abstract class ParticleManagerAsyncMixin {

	@Unique
	private static final Set<Class<?>> unsafeParticles = ConcurrentHashMap.newKeySet();

	@Shadow @Final private Map<ParticleTextureSheet, Queue<Particle>> particles;

	@Shadow protected ClientWorld world;

	@Shadow protected abstract void tickParticle(Particle particle);

	@Shadow protected abstract void addTo(ParticleGroup group, int count);

	@Shadow protected abstract void tickParticles(Collection<Particle> particles);

	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "com/google/common/collect/Maps.newTreeMap (Ljava/util/Comparator;)Ljava/util/TreeMap;"))
	private TreeMap<?, ?> particle_core_setupSynchronizedParticleMap(Comparator<?> comparator, Operation<TreeMap<?, ?>> original) {
		return original.call(comparator); //forge gets to try without synchronization bc they decided they needed to change this map
	}

	@Unique
	private static final Object lock = new Object() { };

	@WrapOperation(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At(value = "INVOKE", target = "java/util/Queue.add (Ljava/lang/Object;)Z"))
	private boolean particle_core_synchronizeParticleAdds(Queue<? extends Particle> instance, Object e, Operation<Boolean> original) {
		synchronized (lock) {
			return original.call(instance, e);
		}
	}

	@SuppressWarnings({"SynchronizeOnNonFinalField"})
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "java/util/Map.forEach (Ljava/util/function/BiConsumer;)V"))
	private void particle_core_asyncParticleTicking(Map<ParticleTextureSheet, Queue<Particle>> instance, BiConsumer<? super ParticleTextureSheet, ? extends Queue<Particle>> v, Operation<Void> original) {
		if (!PcConfig.INSTANCE.getImpl().getAsynchronousTicking().get()) {
			original.call(instance, v);
		} else {
			try {
				var entries = this.particles.entrySet();
				synchronized (this.particles) {
					List<CompletableFuture<TickResult.Results>> futures = new ArrayList<>(entries.size());
					float threshold = PcConfig.INSTANCE.getImpl().getMaxParticlesPerSheet().get() * 0.35f;
					for (Map.Entry<ParticleTextureSheet, Queue<Particle>> entry : entries) {
						this.world.getProfiler().push(entry.getKey().toString());
						if (entry.getValue().isEmpty()) {
							continue;
						}
						if (threshold < entry.getValue().size()) {
							futures.add(CompletableFuture.supplyAsync(() -> asyncTickParticles(entry.getValue())));
						}
					}
					//this is a second loop so that all the async futures can be pushed to their queue above without getting blocked by sync particle ticking
					for (Map.Entry<ParticleTextureSheet, Queue<Particle>> entry : entries) {
						if (entry.getValue().isEmpty() || threshold >= entry.getValue().size()) {
							syncTickParticles(entry.getValue());
							this.world.getProfiler().pop();
						}
					}

					CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
					for (CompletableFuture<TickResult.Results> future : futures) {
						finalizeParticles(future.join());
						this.world.getProfiler().pop();
					}
				}
			} catch (Exception e) {
				PcConfig.INSTANCE.getLogger().error("Asynchronous particle ticking may have encountered a concurrency problem; disabling", e);
				PcConfig.INSTANCE.getImpl().getAsynchronousTicking().validateAndSet(false);
			}
		}
	}

	@Unique
	private void syncTickParticles(Collection<Particle> particles) {
		this.tickParticles(particles);
	}

	@Unique
	private TickResult.Results asyncTickParticles(Collection<Particle> particleCollection) {
		Consumer<Particle> tick = this::tickParticle;

		List<TickResult> results = particleCollection.parallelStream().map((p) -> tickParticleSafe(tick, p)).toList();
		return new TickResult.Results(results, particleCollection);
	}

	@Unique
	private TickResult tickParticleSafe(Consumer<Particle> tick, Particle particle) {
		try {
			if (unsafeParticles.contains(particle.getClass())) {
				return new TickResult(true, particle);
			}
			tick.accept(particle);
		} catch (CrashException e) {
			if (e.getCause() != null) {
				String msg = e.getCause().getMessage();
				if (msg != null && (Objects.equals(msg, "Accessing LegacyRandomSource from multiple threads") || msg.contains("ThreadLocalRandom accessed from a different thread"))) {
					unsafeParticles.add(particle.getClass());
					return new TickResult(true, particle);
				} else if (checkStackTrace(e.getCause())) {
					unsafeParticles.add(particle.getClass());
					return new TickResult(true, particle);
				}
			}
			throw e; //rethrow unknown exception
		}
		return new TickResult(false, particle);
	}

	@Unique
	private boolean checkStackTrace(Throwable e) {
		StackTraceElement[] elements = e.getStackTrace();
		if (elements.length == 0) return false;
		String clazz = elements[0].getClassName();
		if (clazz.contains("ThreadingDetector") || clazz.contains("CheckedThreadLocalRandom")) {
			return true;
		}
		return false;
	}

	@Unique
	private void finalizeParticles(TickResult.Results result) {
		int i = 0;
		for (TickResult tr : result.results()) {
			if (tr.failure()) { //assign failures to the unsafe set and get them ticked
				i += 1;
				this.tickParticle(tr.particle());
			}
		}
		if (i > (result.originalCollection().size() * 2 / 3)) {
			PcConfig.INSTANCE.getLogger().error("Asynchronous particle ticking encountered issues with over 2/3 of particles; disabling");
			PcConfig.INSTANCE.getImpl().getAsynchronousTicking().validateAndSet(false);
		}
		Iterator<? extends Particle> iterator = result.originalCollection().iterator();
		while (iterator.hasNext()) {
			Particle particle = iterator.next();
			if (particle.isAlive()) continue;
			particle.getGroup().ifPresent(group -> this.addTo(group, -1));
			iterator.remove();
		}
	}
}