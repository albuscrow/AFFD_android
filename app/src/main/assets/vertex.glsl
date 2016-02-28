#version 310 es
layout(location=0) uniform mat4 wvpMatrix;
//layout(location=1) uniform mat4 wvMatrix;

layout(location=2) in vec4 vertice;
//layout(location=3) in vec4 normal;

out vec3 varying_normal;
void main() {
//    vertice.w = 1;
    gl_Position = wvpMatrix * vertice;
//    varying_normal = normalize(vec3(wvMatrix * normal));
}