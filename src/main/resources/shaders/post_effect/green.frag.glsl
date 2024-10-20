#version 330

uniform sampler2D uSampler0;
uniform float uTime;

in vec2 vTexCoord;

out vec4 outColor;

void main() {
    vec4 color = texture(uSampler0, vTexCoord);
    outColor = vec4(0.0, sin(color.g + uTime * 0.1) / 2.0 + 0.5, 0.0, color.a);
}