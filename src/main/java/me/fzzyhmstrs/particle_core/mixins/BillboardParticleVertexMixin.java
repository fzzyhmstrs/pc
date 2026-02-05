package me.fzzyhmstrs.particle_core.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fzzyhmstrs.particle_core.VertexContainer;
import me.fzzyhmstrs.particle_core.interfaces.ParticleVertexer;
import me.fzzyhmstrs.particle_core.plugin.PcConditionTester;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = {
                @Condition(type = Condition.Type.TESTER, tester = PcConditionTester.class)
        }
)
@Mixin(value = BillboardParticle.class, priority = 100000)
@Debug(export = true)
abstract class BillboardParticleVertexMixin {

    @WrapOperation(method = "buildGeometry", at = @At(value = "INVOKE", target = "net/minecraft/client/render/VertexConsumer.vertex(DDD)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer particle_core_gatherVertexVertexes(VertexConsumer instance, double x, double y, double z, Operation<VertexConsumer> original, @Share("vertex_container") LocalRef<VertexContainer> ref) {
        if (!(instance instanceof ParticleVertexer)) return original.call(instance, x, y, z);
        VertexContainer c = new VertexContainer();
        c.x = (float) x;
        c.y = (float) y;
        c.z = (float) z;
        ref.set(c);
        return instance;
    }

    @WrapOperation(method = "buildGeometry", at = @At(value = "INVOKE", target = "net/minecraft/client/render/VertexConsumer.texture (FF)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer particle_core_gatherVertexTextures(VertexConsumer instance, float u, float v, Operation<VertexConsumer> original, @Share("vertex_container") LocalRef<VertexContainer> ref) {
        if (!(instance instanceof ParticleVertexer)) return original.call(instance, u, v);
        VertexContainer c = ref.get();
        c.u = u;
        c.v = v;
        return instance;
    }

    @WrapOperation(method = "buildGeometry", at = @At(value = "INVOKE", target = "net/minecraft/client/render/VertexConsumer.color (FFFF)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer particle_core_gatherVertexColors(VertexConsumer instance, float red, float green, float blue, float alpha, Operation<VertexConsumer> original, @Share("vertex_container") LocalRef<VertexContainer> ref) {
        if (!(instance instanceof ParticleVertexer)) return original.call(instance, red, green, blue, alpha);
        VertexContainer c = ref.get();
        c.red = red;
        c.green = green;
        c.blue = blue;
        c.alpha = alpha;
        return instance;
    }

    @WrapOperation(method = "buildGeometry", at = @At(value = "INVOKE", target = "net/minecraft/client/render/VertexConsumer.light (I)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer particle_core_gatherVertexLight(VertexConsumer instance, int i, Operation<VertexConsumer> original, @Share("vertex_container") LocalRef<VertexContainer> ref) {
        if (!(instance instanceof ParticleVertexer)) return original.call(instance, i);
        VertexContainer c = ref.get();
        c.light = i;
        return instance;
    }
    @WrapOperation(method = "buildGeometry", at = @At(value = "INVOKE", target = "net/minecraft/client/render/VertexConsumer.next ()V"))
    private void particle_core_performVertexing(VertexConsumer instance, Operation<Void> original, @Share("vertex_container") LocalRef<VertexContainer> ref) {
        if (!(instance instanceof ParticleVertexer)) {
            original.call(instance);
            return;
        }
        VertexContainer c = ref.get();
        ((ParticleVertexer) instance).particle_core_particleVertex(c.x, c.y, c.z, c.red, c.green, c.blue, c.alpha, c.u, c.v, c.light);
    }
}