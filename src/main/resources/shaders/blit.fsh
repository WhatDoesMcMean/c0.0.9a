#version 330

uniform sampler2D uSampler0;
uniform int uEffect;
uniform vec2 uSize;
uniform float uTime;

in vec2 vTexCoord;

out vec4 outColor;

const float offset = 1.0 / 200.0;

const float LINE_COUNT = 90.0;
const float FREQ = LINE_COUNT * 2.0 * 3.14159;
const float INTENSITY = 0.4;

void main() {
    if (uEffect == 0) {
        outColor = texture(uSampler0, vTexCoord);
    } else if (uEffect == 1) { // Inverted
        outColor = vec4(1.0 - texture(uSampler0, vTexCoord).rgb, 1.0);
    } else if (uEffect == 2) { // GRAYSCALE
        vec4 color = texture(uSampler0, vTexCoord);
        float average = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
        outColor = vec4(average, average, average, 1.0);
    } else if (uEffect == 3) { // BLUR
        vec2 offsets[9] = vec2[](
            vec2(-offset,  offset), // top-left
            vec2( 0.0f,    offset), // top-center
            vec2( offset,  offset), // top-right
            vec2(-offset,  0.0f),   // center-left
            vec2( 0.0f,    0.0f),   // center-center
            vec2( offset,  0.0f),   // center-right
            vec2(-offset, -offset), // bottom-left
            vec2( 0.0f,   -offset), // bottom-center
            vec2( offset, -offset)  // bottom-right
        );

        float kernel[9] = float[](
            1.0 / 16, 2.0 / 16, 1.0 / 16,
            2.0 / 16, 4.0 / 16, 2.0 / 16,
            1.0 / 16, 2.0 / 16, 1.0 / 16
        );

        vec3 sampleTex[9];
        for (int i = 0; i < 9; i++) {
            sampleTex[i] = vec3(texture(uSampler0, vTexCoord.st + offsets[i]));
        }
        vec3 col = vec3(0.0);

        for (int i = 0; i < 9; i++)
            col += sampleTex[i] * kernel[i];

        outColor = vec4(col, 1.0);
    } else if (uEffect == 4) {
        vec4 color = texture(uSampler0, vTexCoord);
        outColor = vec4(0.0, sin(color.g + uTime * 0.1) / 2.0 + 0.5, 0.0, color.a);
    } else if (uEffect == 5) {
        outColor = texture(uSampler0, vTexCoord);

        float screenV = gl_FragCoord.y / uSize.y + uTime * 0.01;
        float scanLine = 1.0 - INTENSITY * step(0.5, mod(screenV * LINE_COUNT, 1.0));

        outColor = vec4(1.0) * vec4(outColor.rgb * scanLine, outColor.a);
    } else {
        outColor = texture(uSampler0, vTexCoord);
    }
}