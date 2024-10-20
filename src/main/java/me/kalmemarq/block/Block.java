package me.kalmemarq.block;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3d;

import com.fasterxml.jackson.databind.JsonNode;

import me.kalmemarq.World;
import me.kalmemarq.particle.Particle;
import me.kalmemarq.particle.ParticleSystem;
import me.kalmemarq.render.MatrixStack;
import me.kalmemarq.render.vertex.BufferBuilder;
import me.kalmemarq.util.BlockHitResult;
import me.kalmemarq.util.Box;
import me.kalmemarq.util.Direction;
import me.kalmemarq.util.IOUtils;
import me.kalmemarq.util.MathUtils;

public class Block {
    public static final Box VOXEL_SHAPE = new Box(0, 0, 0, 1, 1, 1);
    
    public final int numericId;
    public final int[] sideTextures;
    public Model model;

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

        float light = 1.0f;
        if (!(world.isLit(x, y, z) ^ layer != 1)) {
            for (FaceQuad face : model.faces.get(Direction.DOWN)) {
                builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
            }
            
            for (FaceQuad face : model.faces.get(Direction.UP)) {
                builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
            }
            
            for (FaceQuad face : model.faces.get(Direction.NORTH)) {
                builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
            }

            for (FaceQuad face : model.faces.get(Direction.SOUTH)) {
                builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
            }

            for (FaceQuad face : model.faces.get(Direction.WEST)) {
                builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
            }

            for (FaceQuad face : model.faces.get(Direction.EAST)) {
                builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
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

                for (FaceQuad face : model.faces.get(Direction.DOWN)) {
                    builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                    builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                    builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                    builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
                }
            }
        }

        if (shouldRenderTop) {
            float light = world.getBrigthness(x, y + 1, z);
            if (light == 1.0f ^ layer == 1) {
                ++rendered;

                for (FaceQuad face : model.faces.get(Direction.UP)) {
                    builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                    builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                    builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                    builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
                }
            }
        }

        if (shouldRenderNorth) {
            float light = world.getBrigthness(x, y, z - 1) * 0.8f;
            if (light == 0.8f ^ layer == 1) {
                ++rendered;

                for (FaceQuad face : model.faces.get(Direction.NORTH)) {
                    builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                    builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                    builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                    builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
                }
            }
        }

        if (shouldRenderSouth) {
            float light = world.getBrigthness(x, y, z + 1) * 0.8f;
            if (light == 0.8f ^ layer == 1) {
                ++rendered;

                for (FaceQuad face : model.faces.get(Direction.SOUTH)) {
                    builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                    builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                    builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                    builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
                }
            }
        }

        if (shouldRenderWest) {
            float light = world.getBrigthness(x - 1, y, z) * 0.6f;
            if (light == 0.6f ^ layer == 1) {
               ++rendered;

                for (FaceQuad face : model.faces.get(Direction.WEST)) {
                    builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                    builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                    builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                    builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
                }
            }
        }

        if (shouldRenderEast) {
            float light = world.getBrigthness(x + 1, y, z) * 0.6f;
            if (light == 0.6f ^ layer == 1) {
                ++rendered;

                for (FaceQuad face : model.faces.get(Direction.EAST)) {
                    builder.vertex(matrix, face.v0[0], face.v0[1], face.v0[2]).uv(face.v0[3], face.v0[4]).color(light, light, light);
                    builder.vertex(matrix, face.v1[0], face.v1[1], face.v1[2]).uv(face.v1[3], face.v1[4]).color(light, light, light);
                    builder.vertex(matrix, face.v2[0], face.v2[1], face.v2[2]).uv(face.v2[3], face.v2[4]).color(light, light, light);
                    builder.vertex(matrix, face.v3[0], face.v3[1], face.v3[2]).uv(face.v3[3], face.v3[4]).color(light, light, light);
                }
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

    public record Model(Map<Direction, List<FaceQuad>> faces) {
        public static Model load(String name) {
            List<FaceQuad> downFaces = new ArrayList<>();
            List<FaceQuad> upFaces = new ArrayList<>();
            List<FaceQuad> northFaces = new ArrayList<>();
            List<FaceQuad> southFaces = new ArrayList<>();
            List<FaceQuad> westFaces = new ArrayList<>();
            List<FaceQuad> eastFaces = new ArrayList<>();

            try {
                JsonNode node = IOUtils.OBJECT_MAPPER.readTree(Files.readString(IOUtils.getResourcesPath().resolve("models/block/" + name + ".json")));

                if (node.has("parent")) {
                    String parent = node.get("parent").asText();

                    if ("builtin/cross".equals(parent)) {
                        JsonNode textureIndexNode = node.get("texture_index");

                        int txrIdx = textureIndexNode != null ? textureIndexNode.asInt() : 0;
                        float u = (txrIdx % 16) * 16;
                        float v = (txrIdx / 16) * 16;
                        float u0 = u / 256.0f;
                        float v0 = v / 256.0f;
                        float u1 = (u + 16) / 256.0f;
                        float v1 = (v + 16) / 256.0f;
                        
                        float angle = 45.0f;
                        double rads = Math.toRadians(angle);
            
                        for (int r = 0; r < 2; ++r) {
                            float xa = (float)(Math.sin((double)r * Math.PI / (double)2 + rads) * 0.5);
                            float za = (float)(Math.cos((double)r * Math.PI / (double)2 + rads) * 0.5);
                            float x0 = (float)0.5F - xa;
                            float x1 = (float)0.5F + xa;
                            float y0 = (float)0.0F;
                            float y1 = (float)1.0F;
                            float z0 = (float)0.5F - za;
                            float z1 = (float)0.5F + za;

                            FaceQuad quad0 = new FaceQuad(
                                new float[]{x0, y1, z0, u1, v0},
                                new float[]{x1, y1, z1, u0, v0},
                                new float[]{x1, y0, z1, u0, v1},
                                new float[]{x0, y0, z0, u1, v1}
                            );

                            FaceQuad quad1 = new FaceQuad(
                                new float[]{x1, y1, z1, u0, v0},
                                new float[]{x0, y1, z0, u1, v0},
                                new float[]{x0, y0, z0, u1, v1},
                                new float[]{x1, y0, z1, u0, v1}
                            );

                            if (r == 0) {
                                northFaces.add(quad0);
                                southFaces.add(quad1);
                            } else {
                                westFaces.add(quad0);
                                eastFaces.add(quad1);
                            }
                        }
                    }
                }

                if (node.has("cuboids")) {
                    for (JsonNode cuboidNode : node.get("cuboids")) {
                        if (!cuboidNode.hasNonNull("bounds")) {
                            continue;
                        }

                        if (!cuboidNode.hasNonNull("faces")) {
                            continue;
                        }

                        JsonNode boundsNode = cuboidNode.get("bounds");
                        JsonNode facesNode = cuboidNode.get("faces");

                        float x0 = boundsNode.get(0).floatValue();
                        float y0 = boundsNode.get(1).floatValue();
                        float z0 = boundsNode.get(2).floatValue();
                        float x1 = boundsNode.get(3).floatValue();
                        float y1 = boundsNode.get(4).floatValue();
                        float z1 = boundsNode.get(5).floatValue();

                        Iterator<Map.Entry<String, JsonNode>> iter = facesNode.fields();
                        while (iter.hasNext()) {
                            Map.Entry<String, JsonNode> entry = iter.next();
                            JsonNode faceNode = entry.getValue();

                            int txrIdx = faceNode.get("texture_index").intValue();

                            float u = 0;
                            float v = 0;
                            float us = 16;
                            float vs = 16;

                            JsonNode uvNode;

                            switch (entry.getKey()) {
                                case "north":
                                case "south":
                                case "west":
                                case "east":
                                    if (faceNode.has("uv")) {
                                        uvNode = faceNode.get("uv");
                                        u = uvNode.get(0).floatValue();
                                        v = uvNode.get(1).floatValue();
                                        us = uvNode.get(2).floatValue();
                                        vs = uvNode.get(3).floatValue();
                                    } else {
                                        v = 16f - y1;
                                        vs = (y1 - y0);
                                    }
                                    break;
                            }

                            int ug = (txrIdx % 16) * 16;
                            int vg = (txrIdx / 16) * 16;
                            float u0 = (ug + u) / 256.0f;
                            float v0 = (vg + v) / 256.0f;
                            float u1 = (ug + u + us) / 256.0f;
                            float v1 = (vg + v + vs) / 256.0f;
                            int rotation = 0;

                            if (faceNode.has("rotation")) {
                                JsonNode rotationNode = faceNode.get("rotation");
                                rotation = (rotationNode.asInt() / 90) % 4;
                            }

                            switch (entry.getKey()) {
                                case "down":
                                    downFaces.add(FaceQuad.forDown(x0 / 16f, y0 / 16f, z0 / 16f, x1 / 16f, y1 / 16f, z1 / 16f, u0, v0, u1, v1, rotation));
                                    break;
                                case "up":
                                    upFaces.add(FaceQuad.forUp(x0 / 16f, y0 / 16f, z0 / 16f, x1 / 16f, y1 / 16f, z1 / 16f, u0, v0, u1, v1, rotation));
                                    break;
                                case "north":
                                    northFaces.add(FaceQuad.forNorth(x0 / 16f, y0 / 16f, z0 / 16f, x1 / 16f, y1 / 16f, z1 / 16f, u0, v0, u1, v1, rotation));
                                    break;
                                case "south":
                                    southFaces.add(FaceQuad.forSouth(x0 / 16f, y0 / 16f, z0 / 16f, x1 / 16f, y1 / 16f, z1 / 16f, u0, v0, u1, v1, rotation));
                                    break;
                                case "west":
                                    westFaces.add(FaceQuad.forWest(x0 / 16f, y0 / 16f, z0 / 16f, x1 / 16f, y1 / 16f, z1 / 16f, u0, v0, u1, v1, rotation));
                                    break;
                                case "east":
                                    eastFaces.add(FaceQuad.forEast(x0 / 16f, y0 / 16f, z0 / 16f, x1 / 16f, y1 / 16f, z1 / 16f, u0, v0, u1, v1, rotation));
                                    break;
                            };
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<Direction, List<FaceQuad>> map = new IdentityHashMap<>();
            map.put(Direction.DOWN, downFaces.isEmpty() ? Collections.emptyList() : downFaces);
            map.put(Direction.UP, upFaces.isEmpty() ? Collections.emptyList() : upFaces);
            map.put(Direction.NORTH, northFaces.isEmpty() ? Collections.emptyList() : northFaces);
            map.put(Direction.SOUTH, southFaces.isEmpty() ? Collections.emptyList() : southFaces);
            map.put(Direction.WEST, westFaces.isEmpty() ? Collections.emptyList() : westFaces);
            map.put(Direction.EAST, eastFaces.isEmpty() ? Collections.emptyList() : eastFaces);
            return new Model(map);
        }
    }

    public record FaceQuad(float[] v0, float[] v1, float[] v2, float[] v3) {
        public static FaceQuad forDown(float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int rotation) {
            float uV0 = u0; float vV0 = v0;
            float uV1 = u1; float vV1 = v0;
            float uV2 = u1; float vV2 = v1;
            float uV3 = u0; float vV3 = v1;

            if (rotation == 1) {
                uV0 = u1; vV0 = v0;
                uV1 = u0; vV1 = v1;
                uV2 = u0; vV2 = v1;
                uV3 = u1; vV3 = v0;
            } else if (rotation == 2) {
                uV0 = u0; vV0 = v1;
                uV1 = u1; vV1 = v1;
                uV2 = u1; vV2 = v0;
                uV3 = u0; vV3 = v0;
            } else if (rotation == 3) {
                uV0 = u0; vV0 = v1;
                uV1 = u1; vV1 = v0;
                uV2 = u1; vV2 = v0;
                uV3 = u0; vV3 = v1;
            }

            return new FaceQuad(
                new float[]{ x0, y0, z0, uV0, vV0 },
                new float[]{ x1, y0, z0, uV1, vV1 },
                new float[]{ x1, y0, z1, uV2, vV2 },
                new float[]{ x0, y0, z1, uV3, vV3 }
            );
        }
        
        public static FaceQuad forUp(float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int rotation) {
            float uV0 = u0; float vV0 = v0;
            float uV1 = u0; float vV1 = v1;
            float uV2 = u1; float vV2 = v1;
            float uV3 = u1; float vV3 = v0;

            if (rotation == 1) {
                uV0 = u0; vV0 = v1;
                uV1 = u1; vV1 = v1;
                uV2 = u1; vV2 = v0;
                uV3 = u0; vV3 = v0;
            } else if (rotation == 2) {
                uV0 = u0; vV0 = v1;
                uV1 = u0; vV1 = v0;
                uV2 = u1; vV2 = v0;
                uV3 = u1; vV3 = v1;
            } else if (rotation == 3) {
                uV0 = u1; vV0 = v0;
                uV1 = u0; vV1 = v0;
                uV2 = u0; vV2 = v1;
                uV3 = u1; vV3 = v1;
            }

            return new FaceQuad(
                new float[]{ x0, y1, z0, uV0, vV0 },
                new float[]{ x0, y1, z1, uV1, vV1 },
                new float[]{ x1, y1, z1, uV2, vV2 },
                new float[]{ x1, y1, z0, uV3, vV3 }
            );
        }
        
        public static FaceQuad forNorth(float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int rotation) {
            float uV0 = u1; float vV0 = v1;
            float uV1 = u1; float vV1 = v0;
            float uV2 = u0; float vV2 = v0;
            float uV3 = u0; float vV3 = v1;

            if (rotation == 1) {
                uV0 = u0; vV0 = v0;
                uV1 = u1; vV1 = v0;
                uV2 = u1; vV2 = v1;
                uV3 = u0; vV3 = v1;
            } else if (rotation == 2) {
                uV0 = u1; vV0 = v0;
                uV1 = u1; vV1 = v1;
                uV2 = u0; vV2 = v1;
                uV3 = u0; vV3 = v0;
            } else if (rotation == 3) {
                uV0 = u0; vV0 = v1;
                uV1 = u1; vV1 = v1;
                uV2 = u1; vV2 = v0;
                uV3 = u0; vV3 = v0;
            }

            return new FaceQuad(
                new float[]{ x0, y0, z0, uV0, vV0 },
                new float[]{ x0, y1, z0, uV1, vV1 },
                new float[]{ x1, y1, z0, uV2, vV2 },
                new float[]{ x1, y0, z0, uV3, vV3 }
            );
        }

        public static FaceQuad forSouth(float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int rotation) {
            float uV0 = u0; float vV0 = v1;
            float uV1 = u1; float vV1 = v1;
            float uV2 = u1; float vV2 = v0;
            float uV3 = u0; float vV3 = v0;

            if (rotation == 1) {
                uV0 = u1; vV0 = v1;
                uV1 = u1; vV1 = v0;
                uV2 = u0; vV2 = v0;
                uV3 = u0; vV3 = v1;
            } else if (rotation == 2) {
                uV0 = u1; vV0 = v0;
                uV1 = u0; vV1 = v0;
                uV2 = u0; vV2 = v1;
                uV3 = u1; vV3 = v1;
            } else if (rotation == 3) {
                uV0 = u0; vV0 = v0;
                uV1 = u0; vV1 = v1;
                uV2 = u1; vV2 = v1;
                uV3 = u1; vV3 = v0;
            }

            return new FaceQuad(
                new float[]{ x0, y0, z1, uV0, vV0 },
                new float[]{ x1, y0, z1, uV1, vV1 },
                new float[]{ x1, y1, z1, uV2, vV2 },
                new float[]{ x0, y1, z1, uV3, vV3 }
            );
        }

        public static FaceQuad forWest(float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int rotation) {
            float uV0 = u0; float vV0 = v1;
            float uV1 = u1; float vV1 = v1;
            float uV2 = u1; float vV2 = v0;
            float uV3 = u0; float vV3 = v0;

            if (rotation == 1) {
                uV0 = u1; vV0 = v1;
                uV1 = u1; vV1 = v0;
                uV2 = u0; vV2 = v0;
                uV3 = u0; vV3 = v1;
            } else if (rotation == 2) {
                uV0 = u0; vV0 = v0;
                uV1 = u1; vV1 = v0;
                uV2 = u1; vV2 = v1;
                uV3 = u0; vV3 = v1;
            } else if (rotation == 3) {
                uV0 = u0; vV0 = v0;
                uV1 = u0; vV1 = v1;
                uV2 = u1; vV2 = v1;
                uV3 = u1; vV3 = v0;
            }

            return new FaceQuad(
                new float[]{ x0, y0, z0, uV0, vV0 },
                new float[]{ x0, y0, z1, uV1, vV1 },
                new float[]{ x0, y1, z1, uV2, vV2 },
                new float[]{ x0, y1, z0, uV3, vV3 }
            );
        }

        public static FaceQuad forEast(float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float u1, float v1, int rotation) {
            float uV0 = u1; float vV0 = v1;
            float uV1 = u1; float vV1 = v0;
            float uV2 = u0; float vV2 = v0;
            float uV3 = u0; float vV3 = v1;

            if (rotation == 1) {
                uV0 = u1; vV0 = v0;
                uV1 = u0; vV1 = v0;
                uV2 = u0; vV2 = v1;
                uV3 = u1; vV3 = v1;
            } else if (rotation == 2) {
                uV0 = u1; vV0 = v0;
                uV1 = u1; vV1 = v1;
                uV2 = u0; vV2 = v1;
                uV3 = u0; vV3 = v0;
            } else if (rotation == 3) {
                uV0 = u0; vV0 = v1;
                uV1 = u1; vV1 = v1;
                uV2 = u1; vV2 = v0;
                uV3 = u0; vV3 = v0;
            }

            return new FaceQuad(
                new float[]{ x1, y0, z0, uV0, vV0 },
                new float[]{ x1, y1, z0, uV1, vV1 },
                new float[]{ x1, y1, z1, uV2, vV2 },
                new float[]{ x1, y0, z1, uV3, vV3 }
            );
        }
    }
}
