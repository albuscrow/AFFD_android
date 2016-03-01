#version 310 es
layout(location=0) uniform mat4 wvMatrix;
layout(location=1) uniform mat4 wvpMatrix;

layout(location=0) in vec4 attr1;
layout(location=1) in vec4 attr2;

out vec3 varyingNormal;
out vec2 varyingTexCoord;

layout(std430, binding=16) buffer DebugBuffer{
    vec4[] BUFFER_DEBUG_OUTPUT;
};

void main() {
    gl_Position = wvpMatrix * vec4(attr1.xyz, 1);
    varyingNormal = normalize(vec3(wvMatrix * vec4(attr2.xyz, 0)));
    varyingTexCoord = vec2(attr1.w, attr2.w);
    BUFFER_DEBUG_OUTPUT[gl_VertexID] = attr1;
}