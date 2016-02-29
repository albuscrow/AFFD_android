#version 310 es
struct Point {
    vec4 attr1;
    vec4 attr2;
};

struct Triangle {
    ivec4 pointIndex;
    ivec4 adjacentInfo;
};
layout(std430, binding=0) buffer TriangleBuffer{
    Point[] testData;
    Triangle[] testData;
};
layout(local_size_x = 32, local_size_y = 1, local_size_z = 1) in;
void main() {
    testData[gl_GlobalInvocationID.x] = 10087;
    return;
}
