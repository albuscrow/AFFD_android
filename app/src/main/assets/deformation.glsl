#version 310 es

struct SplitTriangle {
    ivec3 pointIndex;
    vec3 adjacent_pn_normal[6];
};

struct SplitPoint {
    vec4 pn_position3_tex1;
    vec4 pn_normal3_tex1;
    vec3 original_position;
};

layout(std430, binding=5) buffer SplitTriangleBuffer{
    SplitTriangle BUFFER_INPUT_TRIANGLES[];
    SplitPoint BUFFER_INPUT_POINTS[];
};

struct OutputPoint {
    vec4 p3t1;
    vec4 n3t1;
};

layout(std430, binding=1) buffer OutputBuffer0{
    OutputPoint[] BUFFER_OUTPUT_POINTS;
};

layout(std430, binding=2) buffer OutputBuffer1{
    int[] BUFFER_OUTPUT_TRIANGLES;
};

layout(std430, binding=16) buffer DebugBuffer{
    vec4[] BUFFER_DEBUG_OUTPUT;
};

int TRIANGLE_NO;
const int SPLIT_TRIANGLE_NUMBER = 0;
layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;
void main() {
    TRIANGLE_NO = int(gl_GlobalInvocationID.x);
    if (TRIANGLE_NO >= SPLIT_TRIANGLE_NUMBER) {
        return;
    }
    //init grobal var
    ivec3 currentPointsIndex = BUFFER_INPUT_TRIANGLES[TRIANGLE_NO].pointIndex;

    for (int i = 0; i < 3; ++i) {
        BUFFER_OUTPUT_POINTS[currentPointsIndex[i]].p3t1 = BUFFER_INPUT_POINTS[currentPointsIndex[i]].pn_position3_tex1;
        BUFFER_OUTPUT_POINTS[currentPointsIndex[i]].n3t1 = BUFFER_INPUT_POINTS[currentPointsIndex[i]].pn_normal3_tex1;
        BUFFER_OUTPUT_TRIANGLES[TRIANGLE_NO * 3 + i] = currentPointsIndex[i];
    }
    return;
}
