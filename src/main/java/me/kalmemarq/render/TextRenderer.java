package me.kalmemarq.render;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import org.joml.Matrix4f;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.kalmemarq.render.vertex.BufferBuilder;
import me.kalmemarq.render.vertex.VertexLayout;
import me.kalmemarq.util.IOUtils;

public class TextRenderer implements Closeable {
    private final Int2ObjectMap<Glyph> glyphs = new Int2ObjectOpenHashMap<>();
    private Texture fontTexture;
    private Shader shader;
    public Matrix4f projectionMatrix = new Matrix4f();
    public Matrix4f modelViewMatrix = new Matrix4f();

    public TextRenderer() {
        this.fontTexture = new Texture();
        this.fontTexture.load(IOUtils.getResourcesPath().resolve("textures/font.png"));

        this.shader = new Shader("entity");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(Files.readString(IOUtils.getResourcesPath().resolve("font.json")));
            int defaultAdvance = node.get("advance").asInt();
            JsonNode charsAdvanceOverride = node.get("chars_advance_override");
            JsonNode chars = node.get("chars");

            for (int i = 0; i < chars.size(); ++i) {
                String line = chars.get(i).asText();

                for (int j = 0; j < line.length(); ++j) {
                    char chr = line.charAt(j);
                    int u = j * 8;
                    int v = i * 8;

                    int advance = defaultAdvance;
                    if (charsAdvanceOverride.has(Character.toString(chr))) {
                        advance = charsAdvanceOverride.get(Character.toString(chr)).asInt();
                    }

                    this.glyphs.put(chr, new Glyph(u / 128.0f, v / 128.0f, (u + 8) / 128.0f, (v + 8) / 128.0f, advance, true));
                }
            }

            JsonNode spaces = node.get("spaces");
            for (Iterator<String> it = spaces.fieldNames(); it.hasNext();) {
                String field = it.next();
                char chr = field.charAt(0);
                int advance = spaces.get(field).asInt();
                this.glyphs.put(chr, new Glyph(0, 0, 0, 0, advance, false));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.glyphs.put(255, new Glyph(120 / 128.0f, 120 / 128.0f, 1f, 1f, 1, false));
    }

    public void drawText(MatrixStack matrices, String text, int x, int y, int color, float hAlign, float vAlign) {
        this.drawText(matrices, text, x, y, color, hAlign, vAlign, ShadowType.NONE);
    }

    public void drawTextWithShadow(MatrixStack matrices, String text, int x, int y, int color, float hAlign, float vAlign) {
        this.drawText(matrices, text, x, y, color, hAlign, vAlign, ShadowType.NORMAL);
    }

    public void drawText(MatrixStack matrices, String text, int x, int y, int color, float hAlign, float vAlign, ShadowType shadowType) {
        Matrix4f matrix = matrices.peek();

        this.shader.bind();
        this.shader.setUniform("uProjection", this.projectionMatrix);
        this.shader.setUniform("uModelView", this.modelViewMatrix);
        this.shader.setUniform("uColor", 1f, 1f, 1f, 1f);
        this.shader.setUniform("uSampler0", 0);
        this.fontTexture.bind(0);

        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = (color >> 24 & 0xFF) / 255.0f;
        if (a < 0.1f) {
            a = 1.0f;
        }

        float sr = r * 0.25f;
        float sg = g * 0.25f;
        float sb = b * 0.25f;
        
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.begin(DrawMode.QUADS, VertexLayout.POS_UV_COLOR);
        BufferBuilder builder = tessellator.getBufferBuilder();

        for (int i = 0; i < text.length(); ++i) {
            Glyph glyph = this.glyphs.get(text.charAt(i));
            if (glyph != null) {
                if (!glyph.renderable) {
                    x += glyph.advance;
                    continue;
                }

                if (shadowType == ShadowType.NORMAL) {
                    ++x; ++y;
                    glyph.render(matrix, builder, x, y, sr, sg, sb, a);
                    --x; --y;
                } else if (shadowType == ShadowType.OUTLINE) {
                    glyph.render(matrix, builder, x - 1, y, sr, sg, sb, a);
                    glyph.render(matrix, builder, x + 1, y, sr, sg, sb, a);
                    glyph.render(matrix, builder, x, y - 1, sr, sg, sb, a);
                    glyph.render(matrix, builder, x, y + 1, sr, sg, sb, a);
                }

                glyph.render(matrix, builder, x, y, r, g, b, a);
                x += glyph.advance;
            }
        }

        tessellator.draw();
    }

    public int getWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); ++i) {
            Glyph glyph = this.glyphs.get(text.charAt(i));
            if (glyph != null) {
                width += glyph.advance;
            }
        }
        return width;
    }

    @Override
    public void close() {
        this.fontTexture.close();
        this.shader.close();
        this.glyphs.clear();
    }

    public enum ShadowType {
        NONE,
        NORMAL,
        OUTLINE
    }

    record Glyph(float u0, float v0, float u1, float v1, float advance, boolean renderable) {
        public void render(Matrix4f matrix, BufferBuilder builder, float x, float y, float r, float g, float b, float a) {
            builder.vertex(matrix, x, y, 0).uv(this.u0, this.v0).color(r, g, b, a);
            builder.vertex(matrix, x, y + 8f, 0).uv(this.u0, this.v1).color(r, g, b, a);
            builder.vertex(matrix, x + 8f, y + 8f, 0).uv(this.u1, this.v1).color(r, g, b, a);
            builder.vertex(matrix, x + 8f, y, 0).uv(this.u1, this.v0).color(r, g, b, a);
        }
    }
}
