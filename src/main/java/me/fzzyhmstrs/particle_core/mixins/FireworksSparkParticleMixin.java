package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.particle_core.interfaces.ParticleCreator;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworkParticles.Starter.class)
public class FireworksSparkParticleMixin {

	// TODO(Ravel): wildcard and regex target are not supported
// TODO(Ravel): wildcard and regex target are not supported
	@WrapOperation(method = "createParticle", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/ParticleManager.addParticle (Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"))
	private Particle particle_core_handleParticleNullability(ParticleEngine instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Particle> original) {
		Particle particle = ((ParticleCreator)instance).particle_core_createSafe(parameters, x, y, z, velocityX, velocityY, velocityZ);
		instance.add(particle);
		return particle;
	}
}