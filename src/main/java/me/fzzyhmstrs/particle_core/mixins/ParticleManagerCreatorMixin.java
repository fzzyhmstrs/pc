package me.fzzyhmstrs.particle_core.mixins;

import me.fzzyhmstrs.particle_core.interfaces.ParticleCreator;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleSpriteManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleManager.class)
public class ParticleManagerCreatorMixin implements ParticleCreator {

	@Shadow @Final private ParticleSpriteManager spriteManager;
	@Shadow protected ClientWorld world;
	@Shadow @Final private Random random;

	@Override
	public <T extends ParticleEffect> Particle particle_core_createSafe(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		@SuppressWarnings("unchecked")
		ParticleFactory<T> particleFactory = (ParticleFactory<T>)this.spriteManager
				.getParticleFactories()
				.get(Registries.PARTICLE_TYPE.getRawId(parameters.getType()));
		return particleFactory == null ? null : particleFactory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ, this.random);
	}
}