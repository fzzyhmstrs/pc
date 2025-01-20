package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.particle.ParticlesMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(WorldRenderer.class)
public class WorldRendererDecreaseMixin {

    @WrapOperation(method = "getRandomParticleSpawnChance", at = @At(value = "INVOKE", target = "net/minecraft/client/option/SimpleOption.getValue ()Ljava/lang/Object;"))
    private <T> T particle_core_reduceParticleSpawnType(SimpleOption<T> instance, Operation<T> original){
        T value = original.call(instance);
        if (value instanceof ParticlesMode) {
            return (T)PcConfig.INSTANCE.getImpl().getReducedParticleSpawnType((ParticlesMode) value);
        }
        return value;
    }

}