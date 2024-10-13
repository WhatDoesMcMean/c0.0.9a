package me.kalmemarq.particle;

import me.kalmemarq.World;
import me.kalmemarq.render.MatrixStack;
import me.kalmemarq.render.vertex.BufferBuilder;
import me.kalmemarq.util.Box;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

public class Particle {
    private final World world;
    public Vector3f position = new Vector3f();
    public Vector3f prevPosition = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public Vector2f size = new Vector2f();
    public boolean toBeRemoved;
    public boolean onGround;
    public Box box;
    public int textureIndex;
    private int lifetime;
    private float uo;
    private float vo;
    private float sizeScale;

    public Particle(World world, float x, float y, float z, float xa, float ya, float za, int textureIndex) {
        this.world = world;
        this.size.set(0.2f, 0.2f);
        this.setPosition(x, y, z);
        this.textureIndex = textureIndex;

        this.velocity.x = xa + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
        this.velocity.y = ya + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
        this.velocity.z = za + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;

        float speed = (float) (Math.random() + Math.random() + 1.0D) * 0.15F;
        float dd = (float) Math.sqrt(this.velocity.x * this.velocity.x + this.velocity.y * this.velocity.y + this.velocity.z * this.velocity.z);

        this.velocity.x = this.velocity.x / dd * speed * 0.7F;
        this.velocity.y = this.velocity.y / dd * speed;
        this.velocity.z = this.velocity.z / dd * speed * 0.7F;

        this.uo = (float) Math.random() * 3.0F;
        this.vo = (float) Math.random() * 3.0F;

        this.sizeScale = (float)(Math.random() * 0.5D + 0.5D);
        this.lifetime = (int)(4.0D / (Math.random() * 0.9D + 0.1D));
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        this.prevPosition.set(this.position);
        this.box = new Box(this.position.x - this.size.x / 2, this.position.y, this.position.z - this.size.x / 2, this.position.x + this.size.x / 2, this.position.y + this.size.y, this.position.z + this.size.x / 2);
    }

    public void tick() {
        this.prevPosition.set(this.position);

        if (this.lifetime-- <= 0) {
            this.toBeRemoved = true;
            return;
        }

        this.velocity.y -= 0.06f;
        this.move(this.velocity.x, this.velocity.y, this.velocity.z);
        this.velocity.mul(0.98f);

        if (this.onGround) {
            this.velocity.mul(0.7f, 0f, 0.7f);
        }
    }

    public void move(float xd, float yd, float zd) {
        float xdOrg = xd;
        float ydOrg = yd;
        float zdOrg = zd;

        List<Box> boxes = this.world.getCubes(this.box.grow(xd, yd, zd));

        for (Box box : boxes) {
            yd = box.clipYCollide(this.box, yd);
        }
        this.box.move(0, yd, 0);

        for (Box box : boxes) {
            xd = box.clipXCollide(this.box, xd);
        }
        this.box.move(xd, 0, 0);

        for (Box box : boxes) {
            zd = box.clipZCollide(this.box, zd);
        }
        this.box.move(0, 0, zd);

        if (ydOrg != yd) {
            this.velocity.y = 0f;
        }

        if (xdOrg != xd) {
            this.velocity.x = 0f;
        }

        if (zdOrg != zd) {
            this.velocity.z = 0f;
        }

        this.onGround = ydOrg != yd && ydOrg < 0f;

        this.position.x = (this.box.minX + this.box.maxX) / 2f;
        this.position.y = this.box.minY + this.size.y;
        this.position.z = (this.box.minZ + this.box.maxZ) / 2f;
    }

    public boolean isLit() {
        return this.world.isLit((int) this.position.x, (int) (this.position.y), (int) this.position.z);
    }

    public void render(MatrixStack matrices, BufferBuilder builder, float xa, float ya, float za, float brightness, float tickDelta) {
        float x = org.joml.Math.lerp(this.prevPosition.x, this.position.x, tickDelta);
        float y = org.joml.Math.lerp(this.prevPosition.y, this.position.y, tickDelta);
        float z = org.joml.Math.lerp(this.prevPosition.z, this.position.z, tickDelta);

        int u = (int) ((this.textureIndex % 16) * 16 + this.uo);
        int v = (int) ((this.textureIndex / 16) * 16 + this.vo);

        float u0 = u / 256.0f;
        float v0 = v / 256.0f;
        float u1 = (u + this.size.x * 16.0f) / 256.0f;
        float v1 = (v + this.size.y * 16.0f) / 256.0f;

        Matrix4f matrix = matrices.peek();

        float r = 0.1F * this.sizeScale;

        builder.vertex(matrix, x - xa * r, y - ya * r, z - za * r).uv(u0, v1).color(brightness, brightness, brightness);
        builder.vertex(matrix, x - xa * r, y + ya * r, z - za * r).uv(u0, v0).color(brightness, brightness, brightness);
        builder.vertex(matrix, x + xa * r, y + ya * r, z + za * r).uv(u1, v0).color(brightness, brightness, brightness);
        builder.vertex(matrix, x + xa * r, y - ya * r, z + za * r).uv(u1, v1).color(brightness, brightness, brightness);
    }
}
