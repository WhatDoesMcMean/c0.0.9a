#version 330

uniform sampler2D uSampler0;

in vec2 vTexCoord;

out vec4 outColor;

void main() {
    vec4 color = texture(uSampler0, vTexCoord);
    outColor = vec4(pow(color.r, 1.4), color.g, pow(color.b, 1.6), color.a);
}