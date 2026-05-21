package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
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
@Mixin(ParticleEngine.class)
public class ParticleEngineTypeMixin {

    @Unique
    private final TagKey<ParticleType<?>> tag = TagKey.create(Registries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath("particle_core","excluded_particles"));

    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void particle_core_excludeAndChanceParticles(ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (PcConfig.INSTANCE.getImpl().getDisableParticles().get()) cir.setReturnValue(null);
        if(BuiltInRegistries.PARTICLE_TYPE.wrapAsHolder(parameters.getType()).is(tag)) cir.setReturnValue(null);
        if(!PcConfig.INSTANCE.getImpl().shouldSpawnParticle(parameters.getType())) cir.setReturnValue(null);
    }
}