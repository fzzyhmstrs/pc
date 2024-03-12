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
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
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
                @Condition("sodium")
        }
)
@Mixin(ParticleManager.class)
public class ParticleManagerRotationMixin implements RotationProvider {

    @Unique
    private final Vector3f[] vectors = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};

    @Override
    public Vector3f[] particle_core_getDefaultBillboardVectors() {
        return new Vector3f[]{
                new Vector3f(vectors[0].x, vectors[0].y, vectors[0].z),
                new Vector3f(vectors[1].x, vectors[1].y, vectors[1].z),
                new Vector3f(vectors[2].x, vectors[2].y, vectors[2].z),
                new Vector3f(vectors[3].x, vectors[3].y, vectors[3].z)};
    }

    @Override
    public void particle_core_setupDefaultBillboardVectors(Camera camera) {
        vectors[0] = new Vector3f(-1.0f, -1.0f, 0.0f);
        vectors[1] = new Vector3f(-1.0f, 1.0f, 0.0f);
        vectors[2] = new Vector3f(1.0f, 1.0f, 0.0f);
        vectors[3] = new Vector3f(1.0f, -1.0f, 0.0f);
        for (var vector : vectors){
            vector.rotate(camera.getRotation());
        }
    }

    @Inject(method = "renderParticles", at = @At("HEAD"))
    private void particle_core_setupDefaultRotations(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci){
        particle_core_setupDefaultBillboardVectors(camera);
    }
}