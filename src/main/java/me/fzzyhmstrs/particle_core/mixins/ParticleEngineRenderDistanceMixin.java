package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
		require = {
				@Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
		}
)
@Mixin(value = ParticleEngine.class, priority = 100000)
public class ParticleEngineRenderDistanceMixin {

	@Inject(method = "extract", at = @At("HEAD"))
	private void particle_core_setupViewDistance(ParticlesRenderState batch, Frustum frustum, Camera camera, float tickProgress, CallbackInfo ci) {
		PcConfig.INSTANCE.getImpl().setupParticleViewDistance();
	}
}