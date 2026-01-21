package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.fzzy_config.util.TriState;
import me.fzzyhmstrs.particle_core.interfaces.BlockPosStorer;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(Particle.class)
public class ParticleCachePosMixin implements BlockPosStorer {

    @Shadow protected double x;
    @Shadow protected double y;
    @Shadow protected double z;
    @Shadow @Final protected ClientWorld world;

    @Unique
    private volatile BlockPos cachedPos = BlockPos.ORIGIN;
    @Unique
    @Nullable
    private volatile BlockState cachedState = null;
    @Unique
    private volatile TriState isEmpty = TriState.DEFAULT;

    @Override
    public void particle_core_tickCachedPos() {
        cachedPos = BlockPos.ofFloored(this.x, this.y, this.z);
        cachedState = null;
        isEmpty = TriState.DEFAULT;
    }

    @Override
    public BlockPos particle_core_getCachedPos() {
        return cachedPos;
    }

    @Override
    public BlockState particle_core_getCachedState() {
        if (cachedState == null) {
            cachedState = this.world.getBlockState(cachedPos);
        }
        return cachedState;
    }

    @Override
    public boolean particle_core_getCachedEmpty() {
        if (isEmpty == TriState.DEFAULT) {
            isEmpty = TriState.Companion.of(particle_core_getCachedState().getCollisionShape(this.world, cachedPos).isEmpty());
        }
        return isEmpty.getAsBoolean();
    }

}