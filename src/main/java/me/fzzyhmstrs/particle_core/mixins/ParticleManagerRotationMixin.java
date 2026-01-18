package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.RotationProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
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

    @Unique
    private final Vector3f[][] vectors = {{ new Vector3f(-1.0f, -1.0f, 0.0f), null, new Vector3f(-1.0f, 1.0f, 0.0f) }, {}, { new Vector3f(1.0f, -1.0f, 0.0f), null, new Vector3f(1.0f, 1.0f, 0.0f) }};

    @Override
    public Vector3f particle_core_getDefaultBillboardVectors(float x, float y) {
        return vectors[(int)x+1][(int)y+1];
    }

    @Override
    public void particle_core_setupDefaultBillboardVectors(Camera camera) {
        vectors[0][0].set(-1.0f, -1.0f, 0.0f).rotate(camera.getRotation());
        vectors[0][2].set(-1.0f, 1.0f, 0.0f).rotate(camera.getRotation());
        vectors[2][0].set(1.0f, -1.0f, 0.0f).rotate(camera.getRotation());
        vectors[2][2].set(1.0f, 1.0f, 0.0f).rotate(camera.getRotation());
    }

    @Inject(method = "renderParticles", at = @At("HEAD"))
    private void particle_core_setupDefaultRotations(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci){
        particle_core_setupDefaultBillboardVectors(camera);
    }
}