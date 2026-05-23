package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.interfaces.FrustumBlacklisted;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.QuadParticleGroup;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(QuadParticleGroup.class)
public abstract class ParticleRendererRenderDistanceMixin {

	// TODO(Ravel): wildcard and regex target are not supported
// TODO(Ravel): wildcard and regex target are not supported
	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/BillboardParticle.render (Lnet/minecraft/client/particle/BillboardParticleSubmittable;Lnet/minecraft/client/render/Camera;F)V"))
	private boolean particle_core_buildGeoIfWithinRenderDistance(SingleQuadParticle instance, QuadParticleRenderState submittable, Camera camera, float tickProgress) {
		return PcConfig.INSTANCE.shouldRenderParticle(
				((ParticleAccessor)instance).getX(),
				((ParticleAccessor)instance).getY(),
				((ParticleAccessor)instance).getZ(),
				camera.position()
		);
	}

}