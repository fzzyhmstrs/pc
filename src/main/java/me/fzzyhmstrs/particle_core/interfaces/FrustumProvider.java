package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.client.renderer.culling.Frustum;

public interface FrustumProvider {

    void particle_core_setFrustum(Frustum frustum);

    Frustum particle_core_getFrustum();

}