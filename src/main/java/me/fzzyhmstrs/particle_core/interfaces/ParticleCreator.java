package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

public interface ParticleCreator {
	<T extends ParticleEffect> Particle particle_core_createSafe(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
}