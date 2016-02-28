#version 310 es
layout(std430, binding=0) buffer TriangleBuffer{
    int[] testData;
};
layout(local_size_x = 32, local_size_y = 1, local_size_z = 1) in;
void main() {
    testData[gl_GlobalInvocationID.x] = 10087;
    return;
}
