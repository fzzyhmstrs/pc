package me.fzzyhmstrs.particle_core.interfaces;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public interface CachedLightProvider {

    Object2IntOpenHashMap<BlockPos> particle_core_getCache();

}