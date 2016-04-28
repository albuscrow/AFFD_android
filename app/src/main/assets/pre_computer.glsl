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
    ivec4 BUFFER_SPLIT_INDEX[];
    vec4 BUFFER_SPLIT_PARAMETER[];
    int BUFFER_OFFSET_NUMBER[];
};

layout(std430, binding=16) buffer DebugBuffer{
    ivec4[] BUFFER_DEBUG_OUTPUT;
};

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

vec3 POSITION[3];
vec3 NORMAL[3];
uvec3 PARAMETER_SWITCH_FLAG;
const float CONST_SPLIT_FACTOR = 0;
const int CONST_MAX_SPLIT_FACTOR = 0;
const int LOOK_UP_TABLE_FOR_I[1] = {0};
int TRIANGLE_NO;

//三角形计数器，因为是多个线程一起产生三角形的，并且存在同一个数组。所以需要这个计数器来同步
layout(binding = 0) uniform atomic_uint ATOMIC_TRIANGLE_COUNTER;

int getOffset(int i, int j, int k){
    if (j - i + 1 <= CONST_MAX_SPLIT_FACTOR - 2 * i){
        return LOOK_UP_TABLE_FOR_I[i - 1] + (j - i) * (i + 1) + k - j;
    } else {
        int qianmianbudongpaishu = max((CONST_MAX_SPLIT_FACTOR - 2 * i), 0);
        int shouxiang = min(i, CONST_MAX_SPLIT_FACTOR - i);
        int xiangshu = j - i - qianmianbudongpaishu;
        return LOOK_UP_TABLE_FOR_I[i - 1] + (i + 1) * qianmianbudongpaishu + xiangshu * (shouxiang + (shouxiang + 1 - xiangshu)) / 2 + k - j;
    }
}

void getSplitePattern(out int indexOffset, out int triangleNumber) {
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
    indexOffset = BUFFER_OFFSET_NUMBER[offset * 2];
    triangleNumber = BUFFER_OFFSET_NUMBER[offset * 2 + 1];
}

vec3 changeParameter(vec3 parameter) {
    if (PARAMETER_SWITCH_FLAG.x == 0u) {
        if (PARAMETER_SWITCH_FLAG.y == 1u) {
            return parameter.xyz;
        } else {
            return parameter.xzy;
        }
    } else if (PARAMETER_SWITCH_FLAG.x == 1u){
        if (PARAMETER_SWITCH_FLAG.y == 2u) {
            return parameter.yzx; //special
        } else {
            return parameter.yxz;
        }
    } else {
        if (PARAMETER_SWITCH_FLAG.y == 0u) {
            return parameter.zxy; //special
        } else {
            return parameter.zyx;
        }
    }
}

vec4 getNormalOrg(vec3 parameter) {
    vec3 result = vec3(0);
    for (int i = 0; i < 3; ++i) {
        result += NORMAL[i] * parameter[i];
    }
    return vec4(normalize(result), 0);
}

vec4 getPositionOrg(vec3 parameter) {
    vec3 result = vec3(0);
    for (int i = 0; i < 3; ++i) {
        result += POSITION[i] * parameter[i];
    }
    return vec4(result, 1);
}

void main() {
    TRIANGLE_NO = int(gl_GlobalInvocationID.x);
    if (TRIANGLE_NO >= BUFFER_INPUT_TRIANGLES.length()) {
        return;
    }
    //init grobal var
    ivec4 currentPointsIndex = BUFFER_INPUT_TRIANGLES[TRIANGLE_NO].pointIndex;

    for (int i = 0; i < 3; ++i) {
        POSITION[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].attr1.xyz;
        NORMAL[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].attr2.xyz;
        Point p = BUFFER_INPUT_POINTS[BUFFER_INPUT_TRIANGLES[TRIANGLE_NO].pointIndex[i]];
//        TRIANGLE_NO = splitTriangleNo;

//        BUFFER_OUTPUT_POINTS[TRIANGLE_NO * 3 + i].attr1 = p.attr1;
//        BUFFER_OUTPUT_POINTS[TRIANGLE_NO * 3 + i].attr2 = p.attr2;
//        BUFFER_OUTPUT_TRIANGLES[TRIANGLE_NO * 3 + i] = TRIANGLE_NO * 3 + i;
    }
    int splitIndexOffset, subTriangleNumber;
    getSplitePattern(splitIndexOffset, subTriangleNumber);
    for (int i = 0; i < subTriangleNumber; ++i) {
        int splitTriangleNo = int(atomicCounterIncrement(ATOMIC_TRIANGLE_COUNTER));
        ivec4 index = BUFFER_SPLIT_INDEX[splitIndexOffset + i];
        for (int j = 0; j < 3; ++j) {
            vec3 parameter = changeParameter(BUFFER_SPLIT_PARAMETER[index[j]].xyz);
            BUFFER_OUTPUT_POINTS[splitTriangleNo * 3 + j].attr1 = getPositionOrg(parameter);
            BUFFER_OUTPUT_POINTS[splitTriangleNo * 3 + j].attr2 = getNormalOrg(parameter);
            BUFFER_OUTPUT_TRIANGLES[splitTriangleNo * 3 + j] = splitTriangleNo * 3 + j;
        }
    }
    return;
}
