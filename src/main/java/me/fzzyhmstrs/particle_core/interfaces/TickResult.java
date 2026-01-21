package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public record TickResult(boolean failure, Particle particle)
