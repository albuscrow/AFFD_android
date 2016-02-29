#version 310 es
struct Point {
    vec4 attr1;
    vec4 attr2;
};

struct Triangle {
    ivec4 pointIndex;
    ivec4 adjacentInfo;
};
layout(std430, binding=0) buffer InputBuffer{
    Point[] points;
    Triangle[] triangle;
};

layout(std430, binding=16) buffer DebugBuffer{
    vec4[] debugOutput;
};
layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;
void main() {
//    testData[gl_GlobalInvocationID.x] = 10087;
    for (int i = 0; i < 4; ++i) {
        debugOutput[i * 2] = points[i].attr1;
        debugOutput[i * 2 + 1] = points[i].attr2;
    }
    return;
}
