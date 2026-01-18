package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ConcurrentHashMap;

public interface CachedLightProvider {
    ConcurrentHashMap<BlockPos, Integer> particle_core_getCache();
}