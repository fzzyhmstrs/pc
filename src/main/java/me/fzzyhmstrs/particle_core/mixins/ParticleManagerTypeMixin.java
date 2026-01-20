package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleManager.class)
public class ParticleManagerTypeMixin {

    @Unique
    private final TagKey<ParticleType<?>> tag = TagKey.of(RegistryKeys.PARTICLE_TYPE, Identifier.of("particle_core","excluded_particles"));

    @Inject(method = "createParticle", at = @At("RETURN"), cancellable = true)
    private void particle_core_excludeAndChanceParticles(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (PcConfig.INSTANCE.getImpl().getDisableParticles().get()) cir.setReturnValue(null);
        if(Registries.PARTICLE_TYPE.getEntry(parameters.getType()).isIn(tag)) cir.setReturnValue(null);
        if(!PcConfig.INSTANCE.getImpl().shouldSpawnParticle(parameters.getType())) cir.setReturnValue(null);
    }
}