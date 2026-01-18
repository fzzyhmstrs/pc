package me.fzzyhmstrs.particle_core;

import com.google.common.collect.AbstractIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class CustomBlockCollisionSpliterator extends AbstractIterator<VoxelShape> {
	private final Box box;
	private final ShapeContext context;
	private final CuboidBlockIterator blockIterator;
	private final BlockPos.Mutable pos;
	private final VoxelShape boxShape;
	private final CollisionView world;
	@Nullable
	private BlockView chunk;
	private long chunkPos;

	public CustomBlockCollisionSpliterator(CollisionView world, @Nullable Entity entity, Box box, boolean checkX, boolean checkY, boolean checkZ) {
		this.context = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
		this.pos = new BlockPos.Mutable();
		this.boxShape = VoxelShapes.cuboid(box);
		this.world = world;
		this.box = box;
		int i = MathHelper.floor(box.minX - 1.0E-7) - (checkX ? 1 : 0);
		int j = MathHelper.floor(box.maxX + 1.0E-7) + (checkX ? 1 : 0);
		int k = MathHelper.floor(box.minY - 1.0E-7) - (checkY ? 1 : 0);
		int l = MathHelper.floor(box.maxY + 1.0E-7) + (checkY ? 1 : 0);
		int m = MathHelper.floor(box.minZ - 1.0E-7) - (checkZ ? 1 : 0);
		int n = MathHelper.floor(box.maxZ + 1.0E-7) + (checkZ ? 1 : 0);
		this.blockIterator = new CuboidBlockIterator(i, k, m, j, l, n);
	}

	@Nullable
	private BlockView getChunk(int x, int z) {
		BlockView blockView;
		int i = ChunkSectionPos.getSectionCoord(x);
		int j = ChunkSectionPos.getSectionCoord(z);
		long l = ChunkPos.toLong(i, j);
		if (this.chunk != null && this.chunkPos == l) {
			return this.chunk;
		}
		this.chunk = blockView = this.world.getChunkAsView(i, j);
		this.chunkPos = l;
		return blockView;
	}

	@Override
	protected VoxelShape computeNext() {
		while (this.blockIterator.step()) {
			BlockView blockView;
			int i = this.blockIterator.getX();
			int j = this.blockIterator.getY();
			int k = this.blockIterator.getZ();
			int l = this.blockIterator.getEdgeCoordinatesCount();
			if (l == 3 || (blockView = this.getChunk(i, k)) == null) continue;
			this.pos.set(i, j, k);
			BlockState blockState = blockView.getBlockState(this.pos);
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);
			if (voxelShape == VoxelShapes.fullCube()) {
				if (!this.box.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) continue;
				return voxelShape.offset(i, j, k);
			}
			VoxelShape voxelShape2 = voxelShape.offset(i, j, k);
			if (voxelShape2.isEmpty() || !VoxelShapes.matchesAnywhere(voxelShape2, this.boxShape, BooleanBiFunction.AND)) continue;
			return voxelShape2;
		}
		return this.endOfData();
	}
}