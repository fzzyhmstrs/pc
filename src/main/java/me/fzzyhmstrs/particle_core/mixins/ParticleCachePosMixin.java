package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.fzzy_config.util.TriState;
import me.fzzyhmstrs.particle_core.interfaces.BlockPosStorer;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
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
    @Shadow @Final protected ClientLevel level;

    @Unique
    private volatile BlockPos cachedPos = BlockPos.ZERO;
    @Unique
    @Nullable
    private volatile BlockState cachedState = null;
    @Unique
    private volatile TriState isEmpty = TriState.DEFAULT;

    @Override
    public void particle_core_tickCachedPos() {
        cachedPos = BlockPos.containing(this.x, this.y, this.z);
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
            cachedState = this.level.getBlockState(cachedPos);
        }
        return cachedState;
    }

    @Override
    public boolean particle_core_getCachedEmpty() {
        if (isEmpty == TriState.DEFAULT) {
            isEmpty = TriState.Companion.of(particle_core_getCachedState().getCollisionShape(this.level, cachedPos).isEmpty());
        }
        return isEmpty.getAsBoolean();
    }

}