package me.fzzyhmstrs.particle_core.mixins;

import me.fzzyhmstrs.particle_core.interfaces.ParticleCreator;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleEngine.class)
public abstract class ParticleManagerCreatorMixin implements ParticleCreator {

	@Invoker("makeParticle")
	public abstract <T extends ParticleOptions> Particle particle_core_makeParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

	@Override
	public <T extends ParticleOptions> Particle particle_core_createSafe(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		return this.particle_core_makeParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
	}
}
