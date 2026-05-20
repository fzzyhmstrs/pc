package me.fzzyhmstrs.particle_core.mixins;

import me.fzzyhmstrs.particle_core.interfaces.ParticleCreator;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleEngine.class)
public class ParticleEngineCreatorMixin implements ParticleCreator {

	@Shadow @Final private ParticleResources resourceManager;
	@Shadow protected ClientLevel level;
	@Shadow @Final private RandomSource random;

	@Override
	public <T extends ParticleOptions> Particle particle_core_createSafe(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		@SuppressWarnings("unchecked")
		ParticleProvider<T> particleFactory = (ParticleProvider<T>)this.resourceManager
				.getProviders()
				.get(BuiltInRegistries.PARTICLE_TYPE.getId(parameters.getType()));
		return particleFactory == null ? null : particleFactory.createParticle(parameters, this.level, x, y, z, velocityX, velocityY, velocityZ, this.random);
	}
}