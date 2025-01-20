package me.fzzyhmstrs.particle_core.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleManager.class)
public class ParticleManagerCachedLightMixin implements CachedLightProvider {

    @Unique
    private final Object2IntLinkedOpenHashMap<BlockPos> particle_core$cachedLightMap = new Object2IntLinkedOpenHashMap<>(64, 0.75f);

    @Override
    public Object2IntLinkedOpenHashMap<BlockPos> particle_core_getCache() {
        return particle_core$cachedLightMap;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void particle_core_setupDefaultRotations(Camera arg, float f, VertexConsumerProvider.Immediate arg2, Frustum frustum, Predicate<ParticleTextureSheet> renderTypePredicate, CallbackInfo ci) {
        particle_core$cachedLightMap.clear();
    }
}