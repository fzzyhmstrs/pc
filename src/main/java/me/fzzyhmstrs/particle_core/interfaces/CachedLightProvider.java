package me.fzzyhmstrs.particle_core.interfaces;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.util.math.BlockPos;

public interface CachedLightProvider {

    Object2IntLinkedOpenHashMap<BlockPos> particle_core_getCache();

}