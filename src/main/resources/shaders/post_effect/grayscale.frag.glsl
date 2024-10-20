#version 330

uniform sampler2D uSampler0;

in vec2 vTexCoord;

out vec4 outColor;

void main() {
    vec4 color = texture(uSampler0, vTexCoord);
    float average = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
    outColor = vec4(average, average, average, 1.0);
}