package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.interfaces.FrustumBlacklisted;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.QuadParticleGroup;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(QuadParticleGroup.class)
public abstract class ParticleRendererFrustumMixin {

	@WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/culling/Frustum;pointInFrustum(DDD)Z"))
	private boolean particle_core_cullParticles(Frustum instance, double x, double y, double z, Operation<Boolean> original, @Local SingleQuadParticle particle, @Local(argsOnly = true) Camera camera) {
		if (instance == null) return true; //fallback if the frustum is being deleted for some reason
		if (((FrustumBlacklisted)particle).particle_core_isBlacklisted()) return true;
		if (PcConfig.INSTANCE.getImpl().passParticleCull())
			return original.call(instance, x, y, z);
		return PcConfig.INSTANCE.getImpl().keepParticle(instance, particle);
	}

}