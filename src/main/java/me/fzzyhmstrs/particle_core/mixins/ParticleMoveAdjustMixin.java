package me.fzzyhmstrs.particle_core.mixins;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fzzyhmstrs.particle_core.CustomBlockCollisionSpliterator;
import me.fzzyhmstrs.particle_core.interfaces.BlockPosStorer;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Particle.class)
public class ParticleMoveAdjustMixin {

	@Shadow protected double x;
	@Shadow protected double y;
	@Shadow protected double z;

	@WrapOperation(method = "move(DDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"), require = 0)
	private Vec3 particle_core_optimizeOpenAirParticleCollisions(@Nullable Entity entity, Vec3 movement, AABB entityBoundingBox, Level world, List<VoxelShape> collisions, Operation<Vec3> original) {
		if (((BlockPosStorer)this).particle_core_getCachedEmpty()) {
			AABB movedBox = entityBoundingBox.move(movement);
			double fY = Mth.floor(y);
			if (fY <= movedBox.minY) { //y ok
				double cY = Mth.ceil(y);
				double fX = Mth.floor(x);
				if (cY >= movedBox.maxY) { //y ok
					if (fX <= movedBox.minX) { //y ok x ok
						double cX = Mth.ceil(x);
						double fZ = Mth.floor(z);
						if (cX >= movedBox.maxX) { //y ok x ok
							if (fZ <= movedBox.minZ) { //y ok x ok z ok
								double cZ = Mth.ceil(z);
								if (cZ >= movedBox.maxZ) { //y ok x ok z ok
									return movement;
								} else { //y ok x ok z bad
									return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, true);
								}
							} else { //y ok x ok z bad
								return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, true);
							}
						} else { //y ok x bad
							if (fZ <= movedBox.minZ) { //y ok x bad z ok
								double cZ = Mth.ceil(z);
								if (cZ >= movedBox.maxZ) { //y ok x bad z ok
									return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, false);
								} else { //y ok x bad z bad
									return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, true);
								}
							} else { //y ok x bad z bad
								return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, true);
							}
						}
					} else { //y ok x bad
						double fZ = Mth.floor(z);
						if (fZ <= movedBox.minZ) { //y ok x bad z ok
							double cZ = Mth.ceil(z);
							if (cZ >= movedBox.maxZ) { //y ok x bad z ok
								return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, false);
							} else { //y ok x bad z bad
								return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, true);
							}
						} else { //y ok x bad z bad
							return adjustMovementForCollisions(movement, entityBoundingBox, world, true, false, true);
						}
					}
				} else { //y bad
					if (fX <= movedBox.minX) { //y bad x ok
						double cX = Mth.ceil(x);
						double fZ = Mth.floor(z);
						if (cX >= movedBox.maxX) { //y bad x ok
							if (fZ <= movedBox.minZ) { //y bad x ok z ok
								double cZ = Mth.ceil(z);
								if (cZ >= movedBox.maxZ) { //y bad x ok z ok
									return adjustMovementForCollisions(movement, entityBoundingBox, world, false, true, false);
								} else { //y bad x ok z bad
									return adjustMovementForCollisions(movement, entityBoundingBox, world, false, true, true);
								}
							} else { //y bad x ok z bad
								return adjustMovementForCollisions(movement, entityBoundingBox, world, false, true, true);
							}
						} else { //y bad x bad
							if (fZ <= movedBox.minZ) { //y bad x bad z ok
								double cZ = Mth.ceil(z);
								if (cZ >= movedBox.maxZ) { //y bad x bad z ok
									return adjustMovementForCollisions(movement, entityBoundingBox, world, true, true, false);
								} //y bad x bad z end
							} //y bad x bad z end
						}
					} else { //y bad x bad
						double fZ = Mth.floor(z);
						if (fZ <= movedBox.minZ) { //y bad x bad z ok
							double cZ = Mth.ceil(z);
							if (cZ >= movedBox.maxZ) { //y bad x bad z ok
								return adjustMovementForCollisions(movement, entityBoundingBox, world, true, true, false);
							}
						}
					}
				}
			} else { //y bad
				double fX = Mth.floor(x);
				if (fX <= movedBox.minX) { //y bad x ok
					double cX = Mth.ceil(x);
					double fZ = Mth.floor(z);
					if (cX >= movedBox.maxX) { //y bad x ok
						if (fZ <= movedBox.minZ) { //y bad x ok z ok
							double cZ = Mth.ceil(z);
							if (cZ >= movedBox.maxZ) { //y bad x ok z ok
								return adjustMovementForCollisions(movement, entityBoundingBox, world, false, true, false);
							} else { //y bad x ok z bad
								return adjustMovementForCollisions(movement, entityBoundingBox, world, false, true, true);
							}
						} else { //y bad x ok z bad
							return adjustMovementForCollisions(movement, entityBoundingBox, world, false, true, true);
						}
					} else { //y bad x bad
						if (fZ <= movedBox.minZ) { //y bad x bad z ok
							double cZ = Mth.ceil(z);
							if (cZ >= movedBox.maxZ) { //y bad x bad z ok
								return adjustMovementForCollisions(movement, entityBoundingBox, world, true, true, false);
							} //y bad x bad z end
						} //y bad x bad z end
					}
				} else { //y bad x bad
					double fZ = Mth.floor(z);
					if (fZ <= movedBox.minZ) { //y bad x bad z ok
						double cZ = Mth.ceil(z);
						if (cZ >= movedBox.maxZ) { //y bad x bad z ok
							return adjustMovementForCollisions(movement, entityBoundingBox, world, true, true, false);
						} //y bad x bad z end
					} //y bad x bad z end
				}
			}
		}
		return original.call(entity, movement, entityBoundingBox, world, collisions);
	}

	@Unique
	private static Vec3 adjustMovementForCollisions(Vec3 movement, AABB entityBoundingBox, Level world, boolean checkX, boolean checkY, boolean checkZ) {
		ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(1);
		builder.addAll(getBlockCollisions(world, entityBoundingBox.expandTowards(movement), checkX, checkY, checkZ));
		return adjustMovementForCollisions(movement, entityBoundingBox, builder.build(), checkX, checkY, checkZ);
	}

	@Unique
	private static Vec3 adjustMovementForCollisions(Vec3 movement, AABB entityBoundingBox, List<VoxelShape> collisions, boolean checkX, boolean checkY, boolean checkZ) {
		if (collisions.isEmpty()) {
			return movement;
		}
		double d = movement.x;
		double e = movement.y;
		double f = movement.z;
		if (checkY && e != 0.0) {
			e = Shapes.collide(Direction.Axis.Y, entityBoundingBox, collisions, e);
			if (e != 0.0) {
				entityBoundingBox = entityBoundingBox.move(0.0, e, 0.0);
			}
		}
		if (checkX && d != 0.0) {
			d = Shapes.collide(Direction.Axis.X, entityBoundingBox, collisions, d);
			if (d != 0.0) {
				entityBoundingBox = entityBoundingBox.move(d, 0.0, 0.0);
			}
		}
		if (checkZ && f != 0.0) {
			f = Shapes.collide(Direction.Axis.Z, entityBoundingBox, collisions, f);
		}
		return new Vec3(d, e, f);
	}

	@Unique
	private static Iterable<VoxelShape> getBlockCollisions(Level world, AABB box, boolean checkX, boolean checkY, boolean checkZ) {
		return () -> new CustomBlockCollisionSpliterator(world, null, box, checkX, checkY, checkZ);
	}
}