package me.fzzyhmstrs.particle_core.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.fzzyhmstrs.particle_core.interfaces.ParticleCreator;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ParticleManager.class)
public class ParticleManagerCreatorMixin implements ParticleCreator {

	@Shadow protected ClientWorld world;
	@Shadow @Final private Int2ObjectMap<ParticleFactory<?>> factories;

	@Override
	public <T extends ParticleEffect> Particle particle_core_createSafe(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		@SuppressWarnings("unchecked")
		ParticleFactory<T> particleFactory = (ParticleFactory<T>)this.factories.get(Registries.PARTICLE_TYPE.getRawId(parameters.getType()));
		return particleFactory == null ? null : particleFactory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
	}
}