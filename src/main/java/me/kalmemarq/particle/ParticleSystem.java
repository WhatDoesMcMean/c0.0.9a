package me.kalmemarq.particle;

import me.kalmemarq.entity.PlayerEntity;
import me.kalmemarq.render.DrawMode;
import me.kalmemarq.render.Frustum;
import me.kalmemarq.render.MatrixStack;
import me.kalmemarq.render.Tessellator;
import me.kalmemarq.render.vertex.BufferBuilder;
import me.kalmemarq.render.vertex.VertexLayout;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

public class ParticleSystem implements Closeable {
    public static int rendered = 0;
    public final List<Particle> particles = new ArrayList<>();

    public void add(Particle p) {
        this.particles.add(p);
    }

    public void tick() {
        for (int i = 0; i < this.particles.size(); ++i) {
            Particle particle = this.particles.get(i);
            particle.tick();
            if (particle.toBeRemoved) {
                this.particles.remove(i--);
            }
        }
    }

    public void render(MatrixStack matrices, PlayerEntity player, Frustum frustum, float tickDelta) {
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.begin(DrawMode.QUADS, VertexLayout.POS_UV_COLOR);
        BufferBuilder builder = tessellator.getBufferBuilder();

        float xa = (float) (-Math.cos(Math.toRadians(player.yaw)));
        float za = (float) (-Math.sin(Math.toRadians(player.yaw)));
        float ya = 1f;

        for (Particle particle : this.particles) {
            if (!frustum.isVisible(particle.box)) continue;
            rendered++;

            particle.render(matrices, builder, xa, ya, za, 0.8f * (particle.isLit() ? 1.0f : 0.6f), tickDelta);
        }

        tessellator.draw();
    }

    @Override
    public void close() {
    }
}
