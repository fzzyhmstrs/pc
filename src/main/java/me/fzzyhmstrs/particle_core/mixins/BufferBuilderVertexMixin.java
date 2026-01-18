package me.fzzyhmstrs.particle_core.mixins;

import me.fzzyhmstrs.particle_core.interfaces.ParticleVertexer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderVertexMixin extends FixedColorVertexConsumer implements ParticleVertexer {

	@Shadow public abstract void putFloat(int index, float value);

	@Shadow public abstract void putByte(int index, byte value);

	@Shadow public abstract void putShort(int index, short value);

	@Shadow private int elementOffset;

	@Shadow public abstract void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ);

	@Override
	public void particle_core_particleVertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int light) {
		if (this.colorFixed) {
			throw new IllegalStateException();
		}
		this.putFloat(0, x);
		this.putFloat(4, y);
		this.putFloat(8, z);
		this.putFloat(12, u);
		this.putFloat(16, v);
		this.putByte(20, (byte)(red * 255.0f));
		this.putByte(21, (byte)(green * 255.0f));
		this.putByte(22, (byte)(blue * 255.0f));
		this.putByte(23, (byte)(alpha * 255.0f));
		this.putShort(24, (short)(light & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F)));
		this.putShort(26, (short)(light >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F)));
		this.elementOffset += 28;
		this.next();
	}
}