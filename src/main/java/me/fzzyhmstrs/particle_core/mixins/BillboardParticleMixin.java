package me.fzzyhmstrs.particle_core.mixins;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.interfaces.RotationProvider;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(
		require = {
				@Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
		}
)
@Mixin(BillboardParticle.class)
public abstract class BillboardParticleMixin {

	@Shadow public abstract BillboardParticle.Rotator getRotator();

	@Redirect(method = "method_60375", at = @At(value = "INVOKE", target = "org/joml/Vector3f.rotate (Lorg/joml/Quaternionfc;)Lorg/joml/Vector3f;"))
	private Vector3f particle_core_rotateBillboardVector(Vector3f instance, Quaternionfc quat, VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n) {
		if (this.getRotator() != BillboardParticle.Rotator.ALL_AXIS)
			return instance.rotate(quat);
		return new Vector3f(((RotationProvider) MinecraftClient.getInstance().particleManager).particle_core_getDefaultBillboardVectors(i, j));
	}


}