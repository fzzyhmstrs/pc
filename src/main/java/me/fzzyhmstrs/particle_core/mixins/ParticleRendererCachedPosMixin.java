package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.BlockPosStorer;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleRenderer.class)
public class ParticleRendererCachedPosMixin {

    @WrapOperation(method = "tickParticle", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.tick ()V"))
    private void particle_core_tickParticlePositions(Particle instance, Operation<Void> original) {
        ((BlockPosStorer) instance).particle_core_tickCachedPos();
        original.call(instance);
    }
}