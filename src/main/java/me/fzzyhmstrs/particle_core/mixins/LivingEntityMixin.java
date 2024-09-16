package me.fzzyhmstrs.particle_core.mixins;

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
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Redirect(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "net/minecraft/world/World.addParticle (Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void particle_core_turnOffPotionParticles(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        boolean bl;
        if(PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get() == PcConfig.PotionDisableType.NONE) bl = true;
        else if(PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get() == PcConfig.PotionDisableType.ALL) bl = false;
        else if ((Object)this instanceof ClientPlayerEntity) {
            bl = (PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get().getIndex() < 1);
        }
        else if ((Object)this instanceof OtherClientPlayerEntity) {
            bl = PcConfig.INSTANCE.getImpl().getTurnOffPotionParticles().get().getIndex() < 2;
        } else {
            bl = true;
        }
        if (bl) {
            instance.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

}