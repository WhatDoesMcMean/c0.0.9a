package me.kalmemarq.block;

public class Blocks {
    public static Block[] blocks;

    public static Block AIR;
    public static Block STONE;
    public static Block GRASS;
    public static Block DIRT;
    public static Block COBBLESTONE;
    public static Block PLANKS;
    public static Block SAPLING;

    public static void initialize() {
        blocks = new Block[7];

        AIR = new Block(0, new int[0]);
        STONE = new Block(1, new int[]{5});
        GRASS = new GrassBlock(2, new int[]{2, 0, 3});
        DIRT = new Block(3, new int[]{2});
        COBBLESTONE = new Block(4, new int[]{1});
        PLANKS = new Block(5, new int[]{4});
        SAPLING = new SaplingBlock(6, 6);

        for (Block block : blocks) {
            if (block.model == null) {
                block.model = switch (block.numericId) {
                    case 1 -> Block.Model.load("stone");
                    case 2 -> Block.Model.load("grass");
                    case 3 -> Block.Model.load("dirt");
                    case 4 -> Block.Model.load("cobblestone");
                    case 5 -> Block.Model.load("planks");
                    case 6 -> Block.Model.load("sapling");
                    default -> null;
                };
            }
        }
    }
}
