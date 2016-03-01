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
    Point[] inputPoints;
    Triangle[] inputTriangles;
};

layout(std430, binding=1) buffer OutputBuffer0{
    Point[] outputPoints;
};

layout(std430, binding=2) buffer OutputBuffer1{
    int[] outputTriangles;
};



layout(std430, binding=16) buffer DebugBuffer{
    ivec4[] debugOutput;
};
layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;
void main() {
    int triangleNo = int(gl_GlobalInvocationID.x);
    if (triangleNo >= inputTriangles.length()) {
        return;
    }

    Point p = inputPoints[inputTriangles[triangleNo].pointIndex.x];
    outputPoints[triangleNo * 3].attr1 = p.attr1;
    outputPoints[triangleNo * 3].attr2 = p.attr2;

    p = inputPoints[inputTriangles[triangleNo].pointIndex.y];
    outputPoints[triangleNo * 3 + 1].attr1 = p.attr1;
    outputPoints[triangleNo * 3 + 1].attr2 = p.attr2;

    p = inputPoints[inputTriangles[triangleNo].pointIndex.z];
    outputPoints[triangleNo * 3 + 2].attr1 = p.attr1;
    outputPoints[triangleNo * 3 + 2].attr2 = p.attr2;

    outputTriangles[triangleNo * 3] = triangleNo * 3;
    outputTriangles[triangleNo * 3 + 1] = triangleNo * 3 + 1;
    outputTriangles[triangleNo * 3 + 2] = triangleNo * 3 + 2;
//    for (int i = 0; i < 2; ++i) {
//        debugOutput[i * 2] = triangles[i].pointIndex;
//        debugOutput[i * 2 + 1] = triangles[i].adjacentInfo;
//    }
    return;
}
