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
    Point[] BUFFER_INPUT_POINTS;
    Triangle[] BUFFER_INPUT_TRIANGLES;
};

layout(std430, binding=1) buffer OutputBuffer0{
    Point[] BUFFER_OUTPUT_POINTS;
};

layout(std430, binding=2) buffer OutputBuffer1{
    int[] BUFFER_OUTPUT_TRIANGLES;
};

layout(std430, binding=3) buffer SplitedData{
    uint BUFFER_OFFSET_NUMBER[];
    uvec4 BUFFER_SPLIT_INDEX[];
    vec4 BUFFER_SPLIT_PARAMETER[];
};

layout(std430, binding=16) buffer DebugBuffer{
    ivec4[] BUFFER_DEBUG_OUTPUT;
};

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

vec3 POSITION[3];
vec3 NORMAL[3];
uvec3 PARAMETER_SWITCH_FLAG;
const float CONST_SPLIT_FACTOR = float(0);
const int MAX_SPLIT_FACTOR = 0;
const int LOOK_UP_TABLE_FOR_I[1] = {0};

int getOffset(int i, int j, int k){
    if (j - i + 1 <= MAX_SPLIT_FACTOR - 2 * i){
        return LOOK_UP_TABLE_FOR_I[i - 1] + (j - i) * (i + 1) + k - j;
    } else {
        int qianmianbudongpaishu = max((MAX_SPLIT_FACTOR - 2 * i), 0);
        int shouxiang = min(i, MAX_SPLIT_FACTOR - i);
        int xiangshu = j - i - qianmianbudongpaishu;
        return LOOK_UP_TABLE_FOR_I[i - 1] + (i + 1) * qianmianbudongpaishu + xiangshu * (shouxiang + (shouxiang + 1 - xiangshu)) / 2 + k - j;
    }
}

void getSplitePattern(out uint indexOffset, out uint triangleNumber) {
    float l01 = distance(POSITION[0], POSITION[1]);
    float l12 = distance(POSITION[1], POSITION[2]);
    float l20 = distance(POSITION[2], POSITION[0]);
    if (l01 < l12 && l01 < l20){
        if (l12 < l20){
            PARAMETER_SWITCH_FLAG = uvec3(0,1,2);
        } else {
            PARAMETER_SWITCH_FLAG = uvec3(1,0,2);
        }
    } else if (l12 < l20){
        if (l01 < l20) {
            PARAMETER_SWITCH_FLAG = uvec3(2,1,0);
        } else {
            PARAMETER_SWITCH_FLAG = uvec3(2,0,1);
        }
    } else {
        if (l12 < l01) {
            PARAMETER_SWITCH_FLAG = uvec3(0,2,1);
        } else {
            PARAMETER_SWITCH_FLAG = uvec3(1,2,0);
        }
    }
    float i, j, k;
    i = min(l01, min(l12, l20));
    k = max(l01, max(l12, l20));
    j = (l01 + l12 + l20) - i - k;
    int i_i, j_i, k_i;
    i_i = int(ceil(i / CONST_SPLIT_FACTOR));
    j_i = int(ceil(j / CONST_SPLIT_FACTOR));
    k_i = int(ceil(k / CONST_SPLIT_FACTOR));

    int offset = getOffset(i_i, j_i, k_i);
    //todo
//    indexOffset = offset_number[offset * 2];
//    triangleNumber = offset_number[offset * 2 + 1];
}


void main() {
    int triangleNo = int(gl_GlobalInvocationID.x);
    if (triangleNo >= BUFFER_INPUT_TRIANGLES.length()) {
        return;
    }
    //init grobal var
    ivec4 currentPointsIndex = BUFFER_INPUT_TRIANGLES[triangleNo].pointIndex;
    for (int i = 0; i < 3; ++i) {
        POSITION[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].attr1.xyz;
        NORMAL[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].attr2.xyz;

        Point p = BUFFER_INPUT_POINTS[BUFFER_INPUT_TRIANGLES[triangleNo].pointIndex[i]];
        BUFFER_OUTPUT_POINTS[triangleNo * 3 + i].attr1 = p.attr1;
        BUFFER_OUTPUT_POINTS[triangleNo * 3 + i].attr2 = p.attr2;

        BUFFER_OUTPUT_TRIANGLES[triangleNo * 3 + i] = triangleNo * 3 + i;
    }
    return;
}
