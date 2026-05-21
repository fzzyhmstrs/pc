// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiClass
package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.BlockPosStorer;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightPreparer;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
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
    @Shadow @Final protected ClientLevel level;

    @Unique
    private int particle_core_cachedLight = -1;

    @WrapOperation(method = "getLightCoords", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;getLightCoords(Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/core/BlockPos;)I"), require = 0)
    private int particle_core_getCachedBrightness(BlockAndLightGetter world, BlockPos pos, Operation<Integer> original) {
        if (particle_core_cachedLight == -1) {
            particle_core_cachedLight = LevelRenderer.getLightCoords(world, pos);
        }
        return particle_core_cachedLight;
    }

    @Override
    public void particle_core_tickLightUpdate() {
        BlockPos blockPos = ((BlockPosStorer)this).particle_core_getCachedPos();
        BlockState state = ((BlockPosStorer)this).particle_core_getCachedState();
        particle_core_cachedLight = ((CachedLightProvider) Minecraft.getInstance().particleEngine).particle_core_getCache().computeIfAbsent(blockPos, (p) -> getLightmap(this.world, state, blockPos));
    }

    @Unique
    private int getLightmap(BlockAndLightGetter world, BlockState state, BlockPos blockPos) {
        return LevelRenderer.getLightCoords(LevelRenderer.BrightnessGetter.DEFAULT, world, state, blockPos);
    }
}