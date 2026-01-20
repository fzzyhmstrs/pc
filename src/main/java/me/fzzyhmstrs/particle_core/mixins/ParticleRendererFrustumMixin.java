package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.interfaces.FrustumBlacklisted;
import me.fzzyhmstrs.particle_core.interfaces.FrustumProvider;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleRenderer;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Submittable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BillboardParticleRenderer.class)
public abstract class ParticleRendererFrustumMixin extends ParticleRenderer<BillboardParticle> {

	@Unique
	private Frustum frustum;

	public ParticleRendererFrustumMixin(ParticleManager particleManager) {
		super(particleManager);
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void particle_core_setupRendererFrustum(Frustum frustum, Camera camera, float tickProgress, CallbackInfoReturnable<Submittable> cir) {
		this.frustum = ((FrustumProvider) this.particleManager).particle_core_getFrustum();
	}

	@WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/BillboardParticle.render (Lnet/minecraft/client/particle/BillboardParticleSubmittable;Lnet/minecraft/client/render/Camera;F)V"))
	private boolean particle_core_cullParticles(BillboardParticle instance, BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
		if (frustum == null) return true; //fallback if the frustum is being deleted for some reason
		if (((FrustumBlacklisted)instance).particle_core_isBlacklisted()) return true;
		return PcConfig.INSTANCE.getImpl().keepParticle(frustum, instance);
	}

}