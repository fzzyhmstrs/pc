package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightPreparer;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleManager.class)
public class ParticleManagerCachedLightMixin implements CachedLightProvider {

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

    @WrapOperation(method = "tickParticle", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.tick ()V"))
    private void particle_core_tickParticleLightUpdates(Particle instance, Operation<Void> original) {
        ((CachedLightPreparer) instance).particle_core_tickLightUpdate();
        original.call(instance);
    }
}