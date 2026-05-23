// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiClass
package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ParticleStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ClientLevel.class)
public class ClientLevelDecreaseMixin {

    // TODO(Ravel): wildcard and regex target are not supported
// TODO(Ravel): wildcard and regex target are not supported
	@WrapOperation(method = "getParticlesMode", at = @At(value = "INVOKE", target = "net/minecraft/client/option/SimpleOption.getValue ()Ljava/lang/Object;"))
    private <T> T particle_core_reduceParticleSpawnType(OptionInstance<T> instance, Operation<T> original) {
        T value = original.call(instance);
        if (value instanceof ParticleStatus) {
            return (T)PcConfig.INSTANCE.getImpl().getReducedParticleSpawnType((ParticleStatus) value);
        }
        return value;
    }

}