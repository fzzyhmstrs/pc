package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.interfaces.FrustumBlacklisted;
import me.fzzyhmstrs.particle_core.interfaces.FrustumProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Frustum;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Environment(EnvType.CLIENT)
@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleManager.class)
public class ParticleManagerFrustumMixin implements FrustumProvider {

    @Unique
    private static Frustum cachedFrustum;

    @Override
    public void particle_core_setFrustum(Frustum frustum) {
        cachedFrustum = frustum;
    }

    @Override
    public Frustum particle_core_getFrustum() {
        return cachedFrustum;
    }

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/client/particle/Particle;)V"))
    private void particle_core_setupBlacklistForParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir, @Local Particle particle) {
        if (PcConfig.INSTANCE.getImpl().shouldBlacklistParticle(parameters.getType())) {
            ((FrustumBlacklisted) particle).particle_core_setBlacklisted();
        }
    }
}