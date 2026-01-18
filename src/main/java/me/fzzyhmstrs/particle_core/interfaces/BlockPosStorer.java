package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface BlockPosStorer {

    void particle_core_tickCachedPos();
    BlockPos particle_core_getCachedPos();
    BlockState particle_core_getCachedState();
    boolean particle_core_getCachedEmpty();
}