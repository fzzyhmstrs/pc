package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.RotationProvider;
import me.fzzyhmstrs.particle_core.interfaces.VectorsStorage;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        },
        conflict = {
                @Condition("sodium"),
                @Condition("embeddium"),
                @Condition("rubidium")
        }
)
@Mixin(ParticleManager.class)
public class ParticleManagerRotationMixin implements RotationProvider {



    @Override
    public Vector3f[] particle_core_getDefaultBillboardVectors() {
        VectorsStorage. particle_core$perParticleVectors[0].set(VectorsStorage. particle_core$vectors[0]);
        VectorsStorage. particle_core$perParticleVectors[1].set(VectorsStorage. particle_core$vectors[1]);
        VectorsStorage. particle_core$perParticleVectors[2].set(VectorsStorage. particle_core$vectors[2]);
        VectorsStorage. particle_core$perParticleVectors[3].set(VectorsStorage. particle_core$vectors[3]);
        return VectorsStorage. particle_core$perParticleVectors;
    }

    @Override
    public void particle_core_setupDefaultBillboardVectors(Camera camera) {
        VectorsStorage.particle_core$vectors[0].set(-1.0f, -1.0f, 0.0f).rotate(camera.getRotation());
        VectorsStorage.particle_core$vectors[1].set(-1.0f, 1.0f, 0.0f).rotate(camera.getRotation());
        VectorsStorage.particle_core$vectors[2].set(1.0f, 1.0f, 0.0f).rotate(camera.getRotation());
        VectorsStorage.particle_core$vectors[3].set(1.0f, -1.0f, 0.0f).rotate(camera.getRotation());
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void particle_core_setupDefaultRotations(MatrixStack arg, VertexConsumerProvider.Immediate arg2, LightmapTextureManager arg3, Camera arg4, float f, Frustum clippingHelper, CallbackInfo ci){
        particle_core_setupDefaultBillboardVectors(arg4);
    }
}