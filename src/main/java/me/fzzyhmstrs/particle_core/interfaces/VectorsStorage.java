package me.fzzyhmstrs.particle_core.interfaces;

import org.joml.Vector3f;

public class VectorsStorage {

	public static final Vector3f[] particle_core$vectors;

	public static final Vector3f[] particle_core$perParticleVectors;

	static {
		particle_core$vectors = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};

		particle_core$perParticleVectors = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};;
	}

}