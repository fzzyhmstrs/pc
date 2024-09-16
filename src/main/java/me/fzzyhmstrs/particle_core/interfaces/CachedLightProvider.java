package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public interface CachedLightProvider {

    HashMap<BlockPos, Integer> particle_core_getCache();

}