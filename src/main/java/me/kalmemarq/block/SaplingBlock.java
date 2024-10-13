package me.kalmemarq.block;

public class SaplingBlock extends Block {
    public SaplingBlock(int numericId, int texture) {
        super(numericId, new int[]{texture});
    }

    @Override
    public boolean hasCollision() {
        return false;
    }
}
