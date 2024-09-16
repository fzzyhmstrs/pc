package me.fzzyhmstrs.particle_core.interfaces;

import net.minecraft.client.render.Camera;
import org.joml.Vector3f;

public interface RotationProvider {

    Vector3f particle_core_getDefaultBillboardVectors(float x, float y);

    void particle_core_setupDefaultBillboardVectors(Camera camera);

}