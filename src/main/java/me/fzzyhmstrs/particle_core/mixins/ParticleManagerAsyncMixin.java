package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.SynchronizedIdentityHashMap;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleGroup;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
@Restriction(
		require = {
				@Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
		}
)
@Mixin(value = ParticleManager.class, priority = 100000)
@Debug(export = true)
public abstract class ParticleManagerAsyncMixin {

	@Shadow @Final private Map<ParticleTextureSheet, ParticleRenderer<? extends Particle>> particles;

	@Shadow protected abstract void addTo(ParticleGroup group, int count);

	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "com/google/common/collect/Maps.newIdentityHashMap ()Ljava/util/IdentityHashMap;"))
	private IdentityHashMap<?, ?> particle_core_setupSynchronizedParticleMap(Operation<IdentityHashMap<?, ?>> original) {
		return new SynchronizedIdentityHashMap<>(original.call());
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
					List<CompletableFuture<Collection<? extends Particle>>> futures = new ArrayList<>(entries.size());
					List<CompletableFuture<Collection<? extends Particle>>> syncFutures = new ArrayList<>(entries.size());
					float threshold = PcConfig.INSTANCE.getImpl().getMaxParticlesPerSheet().get() * 0.35f;
					for (Map.Entry<ParticleTextureSheet, ParticleRenderer<? extends Particle>> entry : entries) {
						Profilers.get().push(entry.getKey().toString());
						if (entry.getValue().isEmpty()) {
							continue;
						}
						if (threshold < entry.getValue().size()) {
							futures.add(CompletableFuture.supplyAsync(() -> asyncTickParticles(entry.getValue())));
						}
					}
					for (Map.Entry<ParticleTextureSheet, ParticleRenderer<?>> entry : entries) {
						if (entry.getValue().isEmpty()) {
							syncFutures.add(CompletableFuture.completedFuture(List.of()));
							continue;
						}
						if (threshold >= entry.getValue().size()) {
							syncFutures.add(CompletableFuture.completedFuture(syncTickParticles(entry.getValue())));
						}
					}
					for (CompletableFuture<Collection<? extends Particle>> future : syncFutures) {
						finalizeParticles(future.join());
						Profilers.get().pop();
					}

					CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
					for (CompletableFuture<Collection<? extends Particle>> future : syncFutures) {
						finalizeParticles(future.join());
						Profilers.get().pop();
					}
				}
			} catch (ConcurrentModificationException e) {
				PcConfig.INSTANCE.getLogger().error("Asynchronous particle ticking encountered a concurrency problem; disabling");
				PcConfig.INSTANCE.getImpl().getAsynchronousTicking().validateAndSet(false);
			} catch (Exception e) {
				throw new RuntimeException("Unhandled exception while ticking particles", e);
			}
		}
	}

	@Unique
	private Collection<Particle> syncTickParticles(ParticleRenderer<?> particles) {
		particles.tick();
		return List.of(); //empty list because we've already fully process particles synchronously
	}

	@Unique
	private Collection<? extends Particle> asyncTickParticles(ParticleRenderer<? extends Particle> particles) {
		particles.getParticles().parallelStream().peek((p) -> ((ParticleRendererAccessor)particles).callTickParticle(p)).forEach((p) -> {});
		return particles.getParticles();
	}

	@Unique
	private void finalizeParticles(Collection<? extends Particle> particles) {
		Iterator<? extends Particle> iterator = particles.iterator();
		while (iterator.hasNext()) {
			Particle particle = iterator.next();
			if (particle.isAlive()) continue;
			particle.getGroup().ifPresent(group -> this.addTo(group, -1));
			iterator.remove();
		}
	}
}