package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.particle_core.interfaces.ParticleCreator;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireworksSparkParticle.FireworkParticle.class)
public class FireworksSparkParticleMixin {

	@WrapOperation(method = "addExplosionParticle", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/ParticleManager.addParticle (Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"))
	private Particle particle_core_handleParticleNullability(ParticleManager instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Particle> original) {
		Particle particle = ((ParticleCreator)instance).particle_core_createSafe(parameters, x, y, z, velocityX, velocityY, velocityZ);
		instance.addParticle(particle);
		return particle;
	}
}