package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.concurrent.ConcurrentHashMap;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleEngine.class)
public class ParticleEngineCachedLightMixin implements CachedLightProvider {

    @Unique
    private volatile ConcurrentHashMap<BlockPos, Integer> cachedLightMap = new ConcurrentHashMap<>(64, 0.75f);

    @Override
    public ConcurrentHashMap<BlockPos, Integer> particle_core_getCache() {
        return cachedLightMap;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void particle_core_clearCache(CallbackInfo ci) {
        int size = cachedLightMap.size();
        cachedLightMap = new ConcurrentHashMap<>(size, 0.75f);
    }
}