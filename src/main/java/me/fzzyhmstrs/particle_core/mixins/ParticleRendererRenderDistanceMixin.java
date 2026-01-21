package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.interfaces.FrustumBlacklisted;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleRenderer;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Submittable;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BillboardParticleRenderer.class)
public abstract class ParticleRendererRenderDistanceMixin {

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/BillboardParticle.render (Lnet/minecraft/client/particle/BillboardParticleSubmittable;Lnet/minecraft/client/render/Camera;F)V"))
	private boolean particle_core_buildGeoIfWithinRenderDistance(BillboardParticle instance, BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
		return PcConfig.INSTANCE.shouldRenderParticle(
				((ParticleAccessor)instance).getX(),
				((ParticleAccessor)instance).getY(),
				((ParticleAccessor)instance).getZ(),
				camera.getCameraPos()
		);
	}

}