#version 310 es

//input
//用于加速计算的控制顶点
layout(std140, binding=0) uniform ControlPointForSample{
    uniform vec3[729] UNIFORM_CONTROL_POINT;
};


layout(std140, binding=1) uniform BSplineBodyInfo{
        uniform uvec3 BSPLINEBODY_ORDER;
        uniform uint BSPLINEBODY_ORDER_PRODUCT;
        uniform uvec3 BSPLINEBODY_CONTROL_POINT_NUM;
        uniform uvec3 BSPLINEBODY_INTERVAL_NUM;
        uniform vec3 BSPLINEBODY_LENGTH;
        uniform vec3 BSPLINEBODY_START_POINT;
        uniform vec3 BSPLINEBODY_STEP;
};

struct SplitTriangle {
    ivec3 pointIndex;
    vec3 adjacent_pn_normal[6];
};

struct SplitPoint {
    vec3 pn_position;
    float texu;
    vec3 pn_normal;
    float texv;
    vec3 original_position;
    uint cage_index;
};

layout(std430, binding=5) buffer SplitTriangleBuffer{
    SplitTriangle BUFFER_INPUT_TRIANGLES[];
    SplitPoint BUFFER_INPUT_POINTS[];
};

struct OutputPoint {
    vec3 position;
    float texu;
    vec3 normal;
    float texv;
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
        BUFFER_OUTPUT_POINTS[currentPointsIndex[i]].position = BUFFER_INPUT_POINTS[currentPointsIndex[i]].pn_position;
        BUFFER_OUTPUT_POINTS[currentPointsIndex[i]].normal = BUFFER_INPUT_POINTS[currentPointsIndex[i]].pn_normal;
        BUFFER_OUTPUT_TRIANGLES[TRIANGLE_NO * 3 + i] = currentPointsIndex[i];
        //temp
        BUFFER_OUTPUT_POINTS[currentPointsIndex[i]].texu = float(BSPLINEBODY_ORDER_PRODUCT);
    }
    return;
}
