package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
@Mixin(BillboardParticle.class)
abstract class BillboardParticleMixin extends Particle {

    protected BillboardParticleMixin(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Inject(method = "buildGeometry", at = @At(value = "INVOKE", target = "net/minecraft/client/particle/BillboardParticle.getSize (F)F"), require = 0)
    private void particle_core_applyStandardRotationVector(VertexConsumer vertexConsumer, Camera camera, float tickDelta, CallbackInfo ci, @Local LocalRef<Vector3f[]> vector3fs) {
        if(this.angle == 0f) {
            Vector3f[] newVectors = ((RotationProvider)MinecraftClient.getInstance().particleManager).particle_core_getDefaultBillboardVectors();
            vector3fs[0] = newVectors[0];
            vector3fs[1] = newVectors[1];
            vector3fs[2] = newVectors[2];
            vector3fs[3] = newVectors[3];
        }
    }

    @WrapWithCondition(method = "buildGeometry", at = @At(value = "INVOKE", target = "org/joml/Vector3f.rotate (Lorg/joml/Quaternionfc;)Lorg/joml/Vector3f;"), require = 0, remap = false)
    private boolean particle_core_onlyRotateIfAngled(Vector3f instance, Quaternionfc quat) {
        return this.angle != 0f;
    }
}