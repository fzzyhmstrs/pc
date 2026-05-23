// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiClass
package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightPreparer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleGroup.class)
public class ParticleGroupBrightnessTickMixin {

	// TODO(Ravel): wildcard and regex target are not supported
// TODO(Ravel): wildcard and regex target are not supported
	@WrapOperation(method = "tickParticle", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.tick ()V"))
	private void particle_core_tickParticleLightUpdates(Particle instance, Operation<Void> original) {
		((CachedLightPreparer) instance).particle_core_tickLightUpdate();
		original.call(instance);
	}

}