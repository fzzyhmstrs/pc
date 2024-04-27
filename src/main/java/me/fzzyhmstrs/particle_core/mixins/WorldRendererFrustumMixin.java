package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.FrustumProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(WorldRenderer.class)
public class WorldRendererFrustumMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "CONSTANT", args = "stringValue=clear"), require = 0)
    private void particle_core_passFrustumToParticleManager1(float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local Frustum frustum){
        ((FrustumProvider)this.client.particleManager).particle_core_setFrustum(frustum);
    }

    /*@Inject(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/ParticleManager.renderParticles (Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/client/render/Camera;F)V"), require = 0)
    private void particle_core_passFrustumToParticleManager2(float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local Frustum frustum){
        ((FrustumProvider)this.client.particleManager).particle_core_setFrustum(frustum);
    }*/
}