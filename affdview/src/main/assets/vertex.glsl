#version 310 es
layout(location=0) uniform mat4 mvMatrix;
layout(location=1) uniform mat4 mvpMatrix;

layout(location=0) in vec4 p3t1;
layout(location=1) in vec4 n3t1;

out vec3 varyingNormal;
out vec2 varyingTexCoord;
out vec4 varyingPosition;

void main() {
    vec4 p = vec4(p3t1.xyz, 1);

    //根据总变换矩阵计算此次绘制此顶点位置
    gl_Position = mvpMatrix * p;

    varyingNormal = normalize((mvMatrix * vec4(n3t1.xyz, 0)).xyz);
    varyingPosition = mvMatrix * p;
    varyingTexCoord = vec2(p3t1.w, n3t1.w);
}
