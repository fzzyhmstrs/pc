package me.fzzyhmstrs.particle_core.mixins;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleGroup.class)
public interface ParticleRendererAccessor {
	@Invoker
	void callTickParticle(Particle particle);
}