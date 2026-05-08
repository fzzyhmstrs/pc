package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticlesRenderState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(ParticleEngine.class)
public abstract class ParticleManagerTypeMixin {

    @Unique
    private final TagKey<ParticleType<?>> tag = TagKey.create(Registries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath("particle_core","excluded_particles"));

    @Shadow
    public abstract void clearParticles();

    @Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
    private void particle_core_excludeAndChanceParticles(ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (particle_core_shouldSkipParticle(parameters)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void particle_core_disableDirectParticleAdds(Particle particle, CallbackInfo ci) {
        if (PcConfig.INSTANCE.getImpl().getDisableParticles().get()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void particle_core_clearParticlesWhenDisabled(CallbackInfo ci) {
        if (PcConfig.INSTANCE.getImpl().getDisableParticles().get()) {
            this.clearParticles();
        }
    }

    @Inject(method = "extract", at = @At("HEAD"), cancellable = true)
    private void particle_core_skipRenderingWhenDisabled(ParticlesRenderState particlesRenderState, Frustum frustum, Camera camera, float tickProgress, CallbackInfo ci) {
        if (PcConfig.INSTANCE.getImpl().getDisableParticles().get()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean particle_core_shouldSkipParticle(ParticleOptions parameters) {
        return PcConfig.INSTANCE.getImpl().getDisableParticles().get()
                || BuiltInRegistries.PARTICLE_TYPE.wrapAsHolder(parameters.getType()).is(tag)
                || !PcConfig.INSTANCE.getImpl().shouldSpawnParticle(parameters.getType());
    }
}
