package me.fzzyhmstrs.particle_core;

import com.google.common.collect.AbstractIterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Cursor3D;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import org.jetbrains.annotations.Nullable;

public class CustomBlockCollisionSpliterator extends AbstractIterator<VoxelShape> {
	private final AABB box;
	private final CollisionContext context;
	private final Cursor3D blockIterator;
	private final BlockPos.MutableBlockPos pos;
	private final VoxelShape boxShape;
	private final CollisionGetter world;
	@Nullable
	private BlockGetter chunk;
	private long chunkPos;

	public CustomBlockCollisionSpliterator(CollisionGetter world, @Nullable Entity entity, AABB box, boolean checkX, boolean checkY, boolean checkZ) {
		this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
		this.pos = new BlockPos.MutableBlockPos();
		this.boxShape = Shapes.create(box);
		this.world = world;
		this.box = box;
		int i = Mth.floor(box.minX - 1.0E-7) - (checkX ? 1 : 0);
		int j = Mth.floor(box.maxX + 1.0E-7) + (checkX ? 1 : 0);
		int k = Mth.floor(box.minY - 1.0E-7) - (checkY ? 1 : 0);
		int l = Mth.floor(box.maxY + 1.0E-7) + (checkY ? 1 : 0);
		int m = Mth.floor(box.minZ - 1.0E-7) - (checkZ ? 1 : 0);
		int n = Mth.floor(box.maxZ + 1.0E-7) + (checkZ ? 1 : 0);
		this.blockIterator = new Cursor3D(i, k, m, j, l, n);
	}

	@Nullable
	private BlockGetter getChunk(int x, int z) {
		BlockGetter blockView;
		int i = SectionPos.blockToSectionCoord(x);
		int j = SectionPos.blockToSectionCoord(z);
		long l = ChunkPos.pack(i, j);
		if (this.chunk != null && this.chunkPos == l) {
			return this.chunk;
		}
		this.chunk = blockView = this.world.getChunkForCollisions(i, j);
		this.chunkPos = l;
		return blockView;
	}

	@Override
	protected VoxelShape computeNext() {
		while (this.blockIterator.advance()) {
			BlockGetter blockView;
			int i = this.blockIterator.nextX();
			int j = this.blockIterator.nextY();
			int k = this.blockIterator.nextZ();
			int l = this.blockIterator.getNextType();
			if (l == 3 || (blockView = this.getChunk(i, k)) == null) continue;
			this.pos.set(i, j, k);
			BlockState blockState = blockView.getBlockState(this.pos);
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, this.pos, this.context);
			if (voxelShape == Shapes.block()) {
				if (!this.box.intersects(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0)) continue;
				return voxelShape.move(i, j, k);
			}
			VoxelShape voxelShape2 = voxelShape.move(i, j, k);
			if (voxelShape2.isEmpty() || !Shapes.joinIsNotEmpty(voxelShape2, this.boxShape, BooleanOp.AND)) continue;
			return voxelShape2;
		}
		return this.endOfData();
	}
}
