#version 330

uniform sampler2D uSampler0;

in vec2 vTexCoord;

out vec4 outColor;

void main() {
    vec4 color = texture(uSampler0, vTexCoord);
    outColor = vec4(color.r, min(pow(color.g, 1.25), 1.0), min(pow(color.b, 1.75), 1.0), color.a);
}