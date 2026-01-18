package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.BlockPosStorer;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightPreparer;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(Particle.class)
public class ParticleBrightnessCacheMixin implements CachedLightPreparer {

    @Shadow protected double x;
    @Shadow protected double y;
    @Shadow protected double z;
    @Shadow @Final protected ClientWorld world;

    @Unique
    private int particle_core_cachedLight = -1;

    @WrapOperation(method = "getBrightness", at = @At(value = "INVOKE", target = "net/minecraft/client/render/WorldRenderer.getLightmapCoordinates (Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"), require = 0)
    private int particle_core_getCachedBrightness(BlockRenderView world, BlockPos pos, Operation<Integer> original) {
        if (particle_core_cachedLight == -1) {
            particle_core_cachedLight = WorldRenderer.getLightmapCoordinates(world, pos);
        }
        return particle_core_cachedLight;
    }

    @Override
    public void particle_core_tickLightUpdate() {
        BlockPos blockPos = ((BlockPosStorer)this).particle_core_getCachedPos();
        BlockState state = ((BlockPosStorer)this).particle_core_getCachedState();
        particle_core_cachedLight = ((CachedLightProvider) MinecraftClient.getInstance().particleManager).particle_core_getCache().computeIfAbsent(blockPos, (p) -> getLightmap(this.world, state, blockPos));
    }

    @Unique
    private int getLightmap(BlockRenderView world, BlockState state, BlockPos blockPos) {
        return WorldRenderer.getLightmapCoordinates(world, state, blockPos);
    }
}