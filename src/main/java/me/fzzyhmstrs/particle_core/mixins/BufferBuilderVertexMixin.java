package me.fzzyhmstrs.particle_core.mixins;

import me.fzzyhmstrs.particle_core.interfaces.ParticleVertexer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderVertexMixin implements ParticleVertexer {

	@Shadow protected abstract long beginVertex();

	@Shadow
	private static void putColor(long pointer, int argb) {
	}

	@Shadow
	private static void putInt(long pointer, int i) {
	}

	@Override
	public void particle_core_particleVertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int light) {
		long l = this.beginVertex();
		MemoryUtil.memPutFloat(l, x);
		MemoryUtil.memPutFloat(l + 4L, y);
		MemoryUtil.memPutFloat(l + 8L, z);
		MemoryUtil.memPutFloat(l + 12L, u);
		MemoryUtil.memPutFloat(l + 16L, v);
		putColor(l + 20L, ColorHelper.Argb.fromFloats(alpha, red, green, blue));
		putInt(l + 24L, light);
	}
}