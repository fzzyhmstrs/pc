package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.RotationProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
		require = {
				@Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
		},
		conflict = {
				@Condition("sodium")
		}
)
@Mixin(BillboardParticle.class)
public abstract class BillboardParticleMixin extends Particle {

	protected BillboardParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Shadow public abstract BillboardParticle.Rotator getRotator();

	@Shadow protected abstract void render(VertexConsumer vertexConsumer, Camera camera, Quaternionf quaternionf, float f);

	@Unique
	private final Quaternionf dummyQuat = new Quaternionf();

	@Inject(method = "render(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V", at = @At("HEAD"), cancellable = true)
	private void particle_core_applyDummyQuat(VertexConsumer vertexConsumer, Camera camera, float tickDelta, CallbackInfo ci) {
		//short circuit the entire buildGeometry process for particles that don't need their geometry rebuilt
		if (this.getRotator() == BillboardParticle.Rotator.ALL_AXIS && this.angle == 0f) {
			this.render(vertexConsumer, camera, dummyQuat, tickDelta);
			ci.cancel();
		}
	}


	@Redirect(method = "renderVertex", at = @At(value = "INVOKE", target = "org/joml/Vector3f.rotate (Lorg/joml/Quaternionfc;)Lorg/joml/Vector3f;"))
	private Vector3f particle_core_rotateBillboardVector(Vector3f instance, Quaternionfc quat, VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n) {
		if (this.getRotator() != BillboardParticle.Rotator.ALL_AXIS || this.angle != 0f)
			return instance.rotate(quat);
		return new Vector3f(((RotationProvider) MinecraftClient.getInstance().particleManager).particle_core_getDefaultBillboardVectors(i, j));
	}


}