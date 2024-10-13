package me.kalmemarq.entity;

import me.kalmemarq.World;

public class ZombieEntity extends Entity {
    public double rot = (float)(Math.random() * Math.PI * 2d);
    public double rotV = (float)(Math.random() + 1d) * 0.01f;
    public final float timeOffs = (float) Math.random() * 1239813f;

    public ZombieEntity(World world) {
        super(world);
    }

    @Override
    public void tick() {
        super.tick();

        this.rot += (float) this.rotV;
        this.rotV *= 0.99d;
        this.rotV += (Math.random() - Math.random()) * Math.random() * Math.random() * 0.01d;

        float xd = (float) Math.sin(this.rot);
        float zd = (float) Math.cos(this.rot);

        if (this.onGround && Math.random() < 0.01d) {
            this.velocity.y = 0.5F;
        }

        this.velocity.y -= 0.08f;

        float speed = this.onGround ? 0.1f : this.canFly ? 0.1f : 0.02f;

        this.moveRelative(xd, zd, speed);
        this.move(this.velocity.x, this.velocity.y, this.velocity.z);

        this.velocity.mul(0.91f, this.canFly ? 0.91f : 0.98f, 0.91f);

        if (this.onGround) {
            this.velocity.mul(0.7f, 0f, 0.7f);
        }
    }
}
