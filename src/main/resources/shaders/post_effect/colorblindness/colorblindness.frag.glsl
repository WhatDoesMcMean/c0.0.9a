#version 330

#include "colorblindness.glsl"

uniform sampler2D uSampler0;

in vec2 vTexCoord;

out vec4 outColor;

void main() {
    outColor = vec4(simulate_colorblindness(texture(uSampler0, vTexCoord).rgb, COLORBLIND_MODE), 1.0);
}