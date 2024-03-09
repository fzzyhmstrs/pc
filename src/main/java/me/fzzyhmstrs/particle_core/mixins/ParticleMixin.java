package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Particle.class)
public class ParticleMixin {

    @WrapOperation(method = "getBrightness", at = @At(value = "INVOKE", target = "net/minecraft/client/render/WorldRenderer.getLightmapCoordinates (Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"))
    private int particle_core_getCachedBrightness(BlockRenderView world, BlockPos pos, Operation<Integer> original) {
        return ((CachedLightProvider) MinecraftClient.getInstance().particleManager).particle_core_getCache().computeIfAbsent(pos,(p) -> original.call(world,p));
    }

}