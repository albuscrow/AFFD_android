#version 310 es
uniform mat4 wvpMatrix;
uniform mat4 wvMatrix;

layout(location=0) in vec4 vertice;
layout(location=1) in vec4 normal;

out vec3 varying_normal;
void main() {
    gl_Position = wvpMatrix * vertice;
    varying_normal = normalize(vec3(wvMatrix * normal));
}