package me.fzzyhmstrs.particle_core.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenCustomHashMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightPreparer;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow protected ClientWorld world;
    @Unique
    private final Object2IntLinkedOpenHashMap<BlockPos> particle_core$cachedLightMap = new Object2IntLinkedOpenHashMap<>(64, 0.75f);

    @Override
    public Object2IntLinkedOpenHashMap<BlockPos> particle_core_getCache() {
        return particle_core$cachedLightMap;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void particle_core_clearCache(CallbackInfo ci){
        cachedLightMap.clear();
    }

    @WrapOperation(method = "tickParticle", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.tick ()V"))
    private void particle_core_onEmitterParticleTick(Particle instance, Operation<Void> original){
        ((CachedLightPreparer) instance).particle_core_tickLightUpdate(this.world);
        original.call(instance);
    }
}