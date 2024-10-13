package me.kalmemarq.block;

import me.kalmemarq.World;
import me.kalmemarq.particle.Particle;
import me.kalmemarq.particle.ParticleSystem;
import me.kalmemarq.render.vertex.BufferBuilder;
import me.kalmemarq.render.MatrixStack;
import me.kalmemarq.util.BlockHitResult;
import me.kalmemarq.util.Box;
import me.kalmemarq.util.Direction;
import me.kalmemarq.util.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.Random;
import java.util.Arrays;

public class Block {
    public static final Box VOXEL_SHAPE = new Box(0, 0, 0, 1, 1, 1);

    public final int numericId;
    public final int[] sideTextures;

    public Block(int numericId, int[] sideTextures) {
        Blocks.blocks[numericId] = this;
        this.numericId = numericId;
        if (sideTextures.length == 1) {
            this.sideTextures = new int[6];
            Arrays.fill(this.sideTextures, sideTextures[0]);
        } else if (sideTextures.length == 3) {
            this.sideTextures = new int[]{sideTextures[0], sideTextures[1], sideTextures[2], sideTextures[2], sideTextures[2], sideTextures[2]};
        } else {
            this.sideTextures = sideTextures;
        }
    }

    public boolean isTickable() {
        return false;
    }

    public void tick(World world, int x, int y, int z, Random random) {
    }

    public boolean hasCollision() {
        return true;
    }

    public void onDestroyed(World world, int x, int y, int z, ParticleSystem particleSystem) {
        for (int xx = 0; xx < 4; ++xx) {
            for (int yy = 0; yy < 4; ++yy) {
                for (int zz = 0; zz < 4; ++zz) {
                    float xp = (float) x + ((float) xx + 0.5F) / 4f;
                    float yp = (float) y + ((float) yy + 0.5F) / 4f;
                    float zp = (float) z + ((float) zz + 0.5F) / 4f;
                    particleSystem.add(new Particle(world, xp, yp, zp, xp - (float)x - 0.5F, yp - (float)y - 0.5F, zp - (float)z - 0.5F, this.sideTextures[2]));
                }
            }
        }
    }

    public int renderCross(World world, MatrixStack matrices, BufferBuilder builder, int x, int y, int z, int layer) {
        int rendered = 0;

        Matrix4f matrix = matrices.peek();

        if (!(world.isLit(x, y, z) ^ layer != 1)) {
            int txrIdx = this.sideTextures[0];
            float u = (txrIdx % 16) * 16;
            float v = (txrIdx / 16) * 16;
            float u0 = u / 256.0f;
            float v0 = v / 256.0f;
            float u1 = (u + 16) / 256.0f;
            float v1 = (v + 16) / 256.0f;

            for (int r = 0; r < 2; ++r) {
				float xa = (float)(Math.sin((double)r * Math.PI / (double)2 + Math.PI * 0.25D) * 0.5D);
				float za = (float)(Math.cos((double)r * Math.PI / (double)2 + Math.PI * 0.25D) * 0.5D);
				float x0 = (float)0.5F - xa;
				float x1 = (float)0.5F + xa;
				float y0 = (float)0.0F;
				float y1 = (float)1.0F;
				float z0 = (float)0.5F - za;
				float z1 = (float)0.5F + za;
				builder.vertex(matrix, x0, y1, z0).uv(u1, v0).color(1f, 1f, 1f);
				builder.vertex(matrix, x1, y1, z1).uv(u0, v0).color(1f, 1f, 1f);
				builder.vertex(matrix, x1, y0, z1).uv(u0, v1).color(1f, 1f, 1f);
				builder.vertex(matrix, x0, y0, z0).uv(u1, v1).color(1f, 1f, 1f);
                
				builder.vertex(matrix, x1, y1, z1).uv(u0, v0).color(1f, 1f, 1f);
				builder.vertex(matrix, x0, y1, z0).uv(u1, v0).color(1f, 1f, 1f);
				builder.vertex(matrix, x0, y0, z0).uv(u1, v1).color(1f, 1f, 1f);
				builder.vertex(matrix, x1, y0, z1).uv(u0, v1).color(1f, 1f, 1f);
			}

            ++rendered;
        }

        return rendered;
    }

    public int render(World world, MatrixStack matrices, BufferBuilder builder, int x, int y, int z, int layer) {
        if (this.numericId == Blocks.SAPLING.numericId) {
            return this.renderCross(world, matrices, builder, x, y, z, layer);
        }
        
        int rendered = 0;

        float x0 = 0f;
        float y0 = 0f;
        float z0 = 0f;
        float x1 = 1f;
        float y1 = 1f;
        float z1 = 1f;

        int u;
        int v;
        float u0;
        float v0;
        float u1;
        float v1;

        Matrix4f matrix = matrices.peek();

        int blockIdBottom = world.getBlockId(x, y - 1, z);
        int blockIdTop = world.getBlockId(x, y + 1, z);
        int blockIdNorth = world.getBlockId(x, y, z - 1);
        int blockIdSouth = world.getBlockId(x, y, z + 1);
        int blockIdWest = world.getBlockId(x - 1, y, z);
        int blockIdEast = world.getBlockId(x + 1, y, z);

        boolean shouldRenderBottom = blockIdBottom == 0 || blockIdBottom == 6;
        boolean shouldRenderTop = blockIdTop == 0 || blockIdTop == 6;
        boolean shouldRenderNorth = blockIdNorth == 0 || blockIdNorth == 6;
        boolean shouldRenderSouth = blockIdSouth == 0 || blockIdSouth == 6;
        boolean shouldRenderWest = blockIdWest == 0 || blockIdWest == 6;
        boolean shouldRenderEast = blockIdEast == 0 || blockIdEast == 6;

        if (shouldRenderBottom) {
            float light = world.getBrigthness(x, y - 1, z);
            if (light == 1.0f ^ layer == 1) {
                ++rendered;
                int txrIdx = this.sideTextures[0];
                u = (txrIdx % 16) * 16;
                v = (txrIdx / 16) * 16;
                u0 = u / 256.0f;
                v0 = v / 256.0f;
                u1 = (u + 16) / 256.0f;
                v1 = (v + 16) / 256.0f;

                builder.vertex(matrix, x0, y0, z0).uv(u0, v0).color(light, light, light);
                builder.vertex(matrix, x1, y0, z0).uv(u1, v0).color(light, light, light);
                builder.vertex(matrix, x1, y0, z1).uv(u1, v1).color(light, light, light);
                builder.vertex(matrix, x0, y0, z1).uv(u0, v1).color(light, light, light);
            }
        }

        if (shouldRenderTop) {
            float light = world.getBrigthness(x, y + 1, z);
            if (light == 1.0f ^ layer == 1) {
                ++rendered;
                int txrIdx = this.sideTextures[1];
                u = (txrIdx % 16) * 16;
                v = (txrIdx / 16) * 16;
                u0 = u / 256.0f;
                v0 = v / 256.0f;
                u1 = (u + 16) / 256.0f;
                v1 = (v + 16) / 256.0f;

                builder.vertex(matrix, x0, y1, z0).uv(u0, v0).color(light, light, light);
                builder.vertex(matrix, x0, y1, z1).uv(u0, v1).color(light, light, light);
                builder.vertex(matrix, x1, y1, z1).uv(u1, v1).color(light, light, light);
                builder.vertex(matrix, x1, y1, z0).uv(u1, v0).color(light, light, light);
            }
        }

        if (shouldRenderNorth) {
            float light = world.getBrigthness(x, y, z - 1) * 0.8f;
            if (light == 0.8f ^ layer == 1) {
                ++rendered;
                int txrIdx = this.sideTextures[2];
                u = (txrIdx % 16) * 16;
                v = (txrIdx / 16) * 16;
                u0 = u / 256.0f;
                v0 = v / 256.0f;
                u1 = (u + 16) / 256.0f;
                v1 = (v + 16) / 256.0f;

                builder.vertex(matrix, x0, y0, z0).uv(u1, v1).color(light, light, light);
                builder.vertex(matrix, x0, y1, z0).uv(u1, v0).color(light, light, light);
                builder.vertex(matrix, x1, y1, z0).uv(u0, v0).color(light, light, light);
                builder.vertex(matrix, x1, y0, z0).uv(u0, v1).color(light, light, light);
            }
        }

        if (shouldRenderSouth) {
            float light = world.getBrigthness(x, y, z + 1) * 0.8f;
            if (light == 0.8f ^ layer == 1) {
                ++rendered;
                int txrIdx = this.sideTextures[3];
                u = (txrIdx % 16) * 16;
                v = (txrIdx / 16) * 16;
                u0 = u / 256.0f;
                v0 = v / 256.0f;
                u1 = (u + 16) / 256.0f;
                v1 = (v + 16) / 256.0f;

                builder.vertex(matrix, x0, y0, z1).uv(u0, v1).color(light, light, light);
                builder.vertex(matrix, x1, y0, z1).uv(u1, v1).color(light, light, light);
                builder.vertex(matrix, x1, y1, z1).uv(u1, v0).color(light, light, light);
                builder.vertex(matrix, x0, y1, z1).uv(u0, v0).color(light, light, light);
            }
        }

        if (shouldRenderWest) {
            float light = world.getBrigthness(x - 1, y, z) * 0.6f;
            if (light == 0.6f ^ layer == 1) {
               ++rendered;
               int txrIdx = this.sideTextures[4];
               u = (txrIdx % 16) * 16;
               v = (txrIdx / 16) * 16;
               u0 = u / 256.0f;
               v0 = v / 256.0f;
               u1 = (u + 16) / 256.0f;
               v1 = (v + 16) / 256.0f;

               builder.vertex(matrix, x0, y0, z0).uv(u0, v1).color(light, light, light);
               builder.vertex(matrix, x0, y0, z1).uv(u1, v1).color(light, light, light);
               builder.vertex(matrix, x0, y1, z1).uv(u1, v0).color(light, light, light);
               builder.vertex(matrix, x0, y1, z0).uv(u0, v0).color(light, light, light);
           }
        }

        if (shouldRenderEast) {
            float light = world.getBrigthness(x + 1, y, z) * 0.6f;
            if (light == 0.6f ^ layer == 1) {
                ++rendered;
                int txrIdx = this.sideTextures[5];
                u = (txrIdx % 16) * 16;
                v = (txrIdx / 16) * 16;
                u0 = u / 256.0f;
                v0 = v / 256.0f;
                u1 = (u + 16) / 256.0f;
                v1 = (v + 16) / 256.0f;

                builder.vertex(matrix, x1, y0, z0).uv(u1, v1).color(light, light, light);
                builder.vertex(matrix, x1, y1, z0).uv(u1, v0).color(light, light, light);
                builder.vertex(matrix, x1, y1, z1).uv(u0, v0).color(light, light, light);
                builder.vertex(matrix, x1, y0, z1).uv(u0, v1).color(light, light, light);
            }
        }

        return rendered;
    }

    public BlockHitResult raytrace(int x, int y, int z, Vector3d start, Vector3d end) {
        Vector3d downVec = MathUtils.intermediateWithY(start, end, VOXEL_SHAPE.minY);
        Vector3d upVec = MathUtils.intermediateWithY(start, end, VOXEL_SHAPE.maxY);
        Vector3d northVec = MathUtils.intermediateWithZ(start, end, VOXEL_SHAPE.minZ);
        Vector3d southVec = MathUtils.intermediateWithZ(start, end, VOXEL_SHAPE.maxZ);
        Vector3d westVec = MathUtils.intermediateWithX(start, end, VOXEL_SHAPE.minX);
        Vector3d eastVec = MathUtils.intermediateWithX(start, end, VOXEL_SHAPE.maxX);

        Vector3d closestHit = null;
        Direction closestSide = null;

        if (VOXEL_SHAPE.containsInXZPlane(downVec)) {
            closestHit = downVec;
            closestSide = Direction.DOWN;
        }

        if (VOXEL_SHAPE.containsInXZPlane(upVec) && (closestHit == null || start.distance(upVec) < start.distance(closestHit))) {
            closestHit = upVec;
            closestSide = Direction.UP;
        }

        if (VOXEL_SHAPE.containsInYZPlane(northVec) && (closestHit == null || start.distance(northVec) < start.distance(closestHit))) {
            closestHit = northVec;
            closestSide = Direction.NORTH;
        }

        if (VOXEL_SHAPE.containsInYZPlane(southVec) && (closestHit == null || start.distance(southVec) < start.distance(closestHit))) {
            closestHit = southVec;
            closestSide = Direction.SOUTH;
        }

        if (VOXEL_SHAPE.containsInXYPlane(westVec) && (closestHit == null || start.distance(westVec) < start.distance(closestHit))) {
            closestHit = westVec;
            closestSide = Direction.WEST;
        }

        if (VOXEL_SHAPE.containsInXYPlane(eastVec) && (closestHit == null || start.distance(eastVec) < start.distance(closestHit))) {
            closestHit = eastVec;
            closestSide = Direction.EAST;
        }

        if (closestHit != null) {
            return new BlockHitResult(x, y, z, closestSide);
        }

        return null;
    }
}
