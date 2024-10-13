#version 330

uniform mat4 uProjection;
uniform mat4 uModelView;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec2 aUV;
layout(location = 2) in vec4 aColor;

out vec2 vUV;
#ifdef FOG
out float vVertexDistance;
#endif
out vec4 vColor;

void main() {
    vec4 pos = uModelView * vec4(aPosition, 1.0);
    gl_Position = uProjection * pos;
    vUV = aUV;
#ifdef FOG
    vVertexDistance = length(pos.xyz);
#endif
    vColor = aColor;
}