package me.fzzyhmstrs.particle_core.mixins;

import net.minecraft.client.render.Frustum;
import org.joml.FrustumIntersection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Frustum.class)
public interface FrustumAccessor {
    @Accessor
    FrustumIntersection getFrustumIntersection();

    @Accessor("x")
    double getX();

    @Accessor("y")
    double getY();

    @Accessor("z")
    double getZ();
}