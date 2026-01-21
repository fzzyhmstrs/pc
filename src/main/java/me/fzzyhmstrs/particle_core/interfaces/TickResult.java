package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderer;

import java.util.Collection;

public record TickResult(boolean failure, Particle particle) {
    public record Results(Collection<TickResult> results, ParticleRenderer<?> originalCollection) {}
}
