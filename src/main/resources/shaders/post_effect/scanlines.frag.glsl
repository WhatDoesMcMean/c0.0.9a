#version 330

uniform sampler2D uSampler0;
uniform float uTime;
uniform vec2 uSize;

in vec2 vTexCoord;

out vec4 outColor;

const float LINE_COUNT = 90.0;
const float FREQ = LINE_COUNT * 2.0 * 3.14159;
const float INTENSITY = 0.4;

void main() {
    outColor = texture(uSampler0, vTexCoord);

    float screenV = gl_FragCoord.y / uSize.y + uTime * 0.01;
    float scanLine = 1.0 - INTENSITY * step(0.5, mod(screenV * LINE_COUNT, 1.0));

    outColor = vec4(outColor.rgb * scanLine, outColor.a);
}