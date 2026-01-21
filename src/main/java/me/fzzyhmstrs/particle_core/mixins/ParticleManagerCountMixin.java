package me.fzzyhmstrs.particle_core.mixins;

import com.google.common.collect.EvictingQueue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

@Restriction(
		require = {
				@Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
		}
)
@Mixin(value = ParticleManager.class, priority = 100000)
public class ParticleManagerCountMixin {

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "java/util/Map.computeIfAbsent (Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"))
	private Object particle_core_particleCount(Map<ParticleTextureSheet, Queue<Particle>> instance, Object key, Function<? super ParticleTextureSheet, ? extends Queue<Particle>> function, Operation<Queue<Particle>> original) {
		return instance.computeIfAbsent((ParticleTextureSheet) key, (k) -> EvictingQueue.create(PcConfig.INSTANCE.getImpl().getMaxParticlesPerSheet().get()));
	}
}