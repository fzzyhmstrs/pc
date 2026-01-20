package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightPreparer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleRenderer.class)
public class ParticleRendererBrightnessTickMixin {

	@WrapOperation(method = "tickParticle", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.tick ()V"))
	private void particle_core_tickParticleLightUpdates(Particle instance, Operation<Void> original) {
		((CachedLightPreparer) instance).particle_core_tickLightUpdate();
		original.call(instance);
	}

}