package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.PcConfig;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Restriction(
		require = {
				@Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
		}
)
@Mixin(value = ParticleManager.class, priority = 100000)
public class ParticleManagerRenderDistanceMixin {

	@Inject(method = "renderParticleType", at = @At("HEAD"))
	private static void particle_core_setupViewDistance(Camera arg, float f, VertexConsumerProvider.Immediate arg2, ParticleTextureSheet arg3, Queue<Particle> queue, Frustum frustum, CallbackInfo ci) {
		PcConfig.INSTANCE.getImpl().setupParticleViewDistance();
	}

	@WrapWithCondition(method = "renderParticleType", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/Particle.render (Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
	private static boolean particle_core_buildGeoIfWithinRenderDistance(Particle instance, VertexConsumer vertexConsumer, Camera camera, float v) {
		return PcConfig.INSTANCE.shouldRenderParticle(
				((ParticleAccessor)instance).getX(),
				((ParticleAccessor)instance).getY(),
				((ParticleAccessor)instance).getZ(),
				camera.getPos()
		);
	}

}