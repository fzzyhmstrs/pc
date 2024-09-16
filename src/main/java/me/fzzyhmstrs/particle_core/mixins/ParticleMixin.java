package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(Particle.class)
public class ParticleMixin {

    @Shadow @Final protected ClientWorld world;

    @Redirect(method = "getBrightness", at = @At(value = "INVOKE", target = "net/minecraft/client/render/WorldRenderer.getLightmapCoordinates (Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"))
    private int particle_core_getCachedBrightness(BlockRenderView world, BlockPos pos) {
        return ((CachedLightProvider) MinecraftClient.getInstance().particleManager).particle_core_getCache().computeIfAbsent(pos, (p) -> WorldRenderer.getLightmapCoordinates(world, pos));
    }

}