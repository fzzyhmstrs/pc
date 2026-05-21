package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapWithCondition(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private boolean particle_core_turnOffPotionParticles(Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if (PcConfig.INSTANCE.shouldDisablePotionParticle(PcConfig.PotionDisableType.NONE)) return true;
        if (PcConfig.INSTANCE.shouldDisablePotionParticle(PcConfig.PotionDisableType.ALL)) return false;
        if ((Object)this instanceof RemotePlayer) {
            return !PcConfig.INSTANCE.shouldDisablePotionParticle(PcConfig.PotionDisableType.OTHER_PLAYER);
        }
        if ((Object)this instanceof LocalPlayer) {
            return !PcConfig.INSTANCE.shouldDisablePotionParticle(PcConfig.PotionDisableType.SELF);
        }
        if ((Object)this instanceof Mob) {
            return !PcConfig.INSTANCE.shouldDisablePotionParticle(PcConfig.PotionDisableType.MOBS);
        }
        return true;
    }
}