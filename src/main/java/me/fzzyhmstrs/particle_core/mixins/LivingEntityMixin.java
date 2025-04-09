package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapWithCondition(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private boolean particle_core_turnOffPotionParticles(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        if(PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get() == PcConfig.PotionDisableType.NONE) return true;
        if(PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get() == PcConfig.PotionDisableType.ALL) return false;
        if ((Object)this instanceof ClientPlayerEntity) {
            return (PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get().getIndex() < 1);
        }
        if ((Object)this instanceof OtherClientPlayerEntity) {
            return PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get().getIndex() < 2;
        }
        return true;
    }

}