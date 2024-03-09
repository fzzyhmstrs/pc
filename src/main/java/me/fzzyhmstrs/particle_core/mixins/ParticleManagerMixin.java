package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.particle_core.interfaces.CachedLightProvider;
import me.fzzyhmstrs.particle_core.interfaces.FrustumProvider;
import me.fzzyhmstrs.particle_core.interfaces.RotationProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin implements FrustumProvider, RotationProvider, CachedLightProvider {

    @Unique
    private Frustum frustum;

    @Unique
    private final Vector3f[] vectors = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};

    @Unique
    private final HashMap<BlockPos,Integer> cachedLightMap = new HashMap<BlockPos,Integer>();

    @Override
    public void particle_core_setFrustum(Frustum frustum) {
        this.frustum = frustum;
    }

    @Override
    public Vector3f[] particle_core_getDefaultBillboardVectors() {
        return new Vector3f[]{
                new Vector3f(vectors[0].x, vectors[0].y, vectors[0].z),
                new Vector3f(vectors[1].x, vectors[1].y, vectors[1].z),
                new Vector3f(vectors[2].x, vectors[2].y, vectors[2].z),
                new Vector3f(vectors[3].x, vectors[3].y, vectors[3].z)};
    }

    @Override
    public HashMap<BlockPos, Integer> particle_core_getCache() {
        return cachedLightMap;
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
        cachedLightMap.clear();
    }

    @WrapWithCondition(method = "renderParticles", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.buildGeometry (Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    private boolean particle_core_cullParticles(Particle instance, VertexConsumer vertexConsumer, Camera camera, float v){
        //return frustum.isVisible(instance.getBoundingBox());
        return ((FrustumAccessor)frustum).getFrustumIntersection().testPoint(
                (float)(((ParticleAccessor)instance).getX() - ((FrustumAccessor)frustum).getX()),
                (float)(((ParticleAccessor)instance).getY() - ((FrustumAccessor)frustum).getY()),
                (float)(((ParticleAccessor)instance).getZ() - ((FrustumAccessor)frustum).getZ())
        );
    }
}