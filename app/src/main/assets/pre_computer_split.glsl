#version 310 es

layout(std140, binding=1) uniform BSplineBodyInfo{
    uniform uvec3 BSPLINEBODY_ORDER;
    uniform uvec3 BSPLINEBODY_CONTROL_POINT_NUM;
    uniform uvec3 BSPLINEBODY_INTERVAL_NUM;
    uniform vec3 BSPLINEBODYL_ENGTH;
    uniform vec3 BSPLINEBODYL_START_POINT;
    uniform vec3 BSPLINEBODYL_STEP;
};

struct InputPoint {
    vec3 position;
    float texu;
    vec3 normal;
    float texv;
};

struct InputTriangle {
    ivec3 pointIndex;
    ivec3 adjacentInfo;
};
layout(std430, binding=0) buffer InputBuffer{
    InputPoint BUFFER_INPUT_POINTS[];
    InputTriangle BUFFER_INPUT_TRIANGLES[];
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
    SplitTriangle BUFFER_OUTPUT_TRIANGLES[];
    SplitPoint BUFFER_OUTPUT_POINTS[];
};

layout(std430, binding=3) buffer SplitedData{
    vec3 BUFFER_SPLIT_PARAMETER[];
    ivec3 BUFFER_SPLIT_TRIANGLE_INDEX[];
    ivec4 BUFFER_OFFSET_NUMBER[];
    int BUFFER_SPLIT_POINT_INDEX[];
};

struct PNTriangle {
    vec3 normalControlPoint[6];
    vec3 positionControlPoint[10];
};
layout(std430, binding=4) buffer PN_TRIANGLE{
    PNTriangle[] BUFFER_INPUT_PN_TRIANGLE;
};

layout(std430, binding=16) buffer DebugBuffer{
    vec4[] BUFFER_DEBUG_OUTPUT;
};

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

//global data
vec3 POSITION[3];
vec3 NORMAL[3];
ivec3 ADJACENCY_TRIANGLE_INDEX = ivec3(-1);
ivec3 ADJACENCY_TRIANGLE_EDGE = ivec3(-1);
vec3 PN_TRIANGLE_POSITION[10];
vec3 PN_TRIANGLE_NORMAL[6];
uvec3 PARAMETER_SWITCH_FLAG;
const float CONST_SPLIT_FACTOR = 0;
const int CONST_MAX_SPLIT_FACTOR = 0;
const int LOOK_UP_TABLE_FOR_I[1] = {0};
const float ZERO = 0.000001;
const vec3 ZERO3 = vec3(ZERO);
const vec4 ZERO4 = vec4(ZERO);
int TRIANGLE_NO;

//三角形计数器，因为是多个线程一起产生三角形的，并且存在同一个数组。所以需要计数器来同步
layout(binding = 0) uniform atomic_uint ATOMIC_TRIANGLE_COUNTER;
layout(binding = 1) uniform atomic_uint ATOMIC_POINT_COUNTER;

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

void getSplitePattern(out int pointStart, out int pointEnd, out int triangleStart, out int triangleEnd) {
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
    vec3 ijk;
    ijk[0] = min(l01, min(l12, l20));
    ijk[2] = max(l01, max(l12, l20));
    ijk[1] = (l01 + l12 + l20) - ijk[0] - ijk[2];
    ivec3 ijk_int;
    for (int i = 0; i < 3; ++i) {
        ijk_int[i] = int(ceil(ijk[i] / CONST_SPLIT_FACTOR));
    }

    int offset = getOffset(ijk_int[0], ijk_int[1], ijk_int[2]);
    pointStart = BUFFER_OFFSET_NUMBER[offset].x;
    pointEnd = BUFFER_OFFSET_NUMBER[offset].y;
    triangleStart = BUFFER_OFFSET_NUMBER[offset].z;
    triangleEnd = BUFFER_OFFSET_NUMBER[offset].w;
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

void genPNTriangle(){
    for (int i = 0; i < 10; ++i) {
        PN_TRIANGLE_POSITION[i] = BUFFER_INPUT_PN_TRIANGLE[TRIANGLE_NO].positionControlPoint[i];
    }
    for (int i = 0; i < 6; ++i) {
        PN_TRIANGLE_NORMAL[i] = BUFFER_INPUT_PN_TRIANGLE[TRIANGLE_NO].normalControlPoint[i];
    }
}

const vec4 factorial_temp = vec4(1,1,2,6);
float factorial(int n) {
    return factorial_temp[n];
}

float power(float b, int n) {
    if (n == 0) {
        return 1.0;
    }
    if (b < ZERO) {
        return 0.0;
    }
    return pow(b, float(n));
}

// 根据 parameter 获得PNTriangle中的位置
vec3 getPNPosition(vec3 parameter) {
    vec3 result = vec3(0);
    int ctrlPointIndex = 0;
    for (int i = 3; i >=0; --i) {
        for (int j = 3 - i; j >= 0; --j) {
            int k = 3 - i - j;
            float n = 6.0 * power(parameter.x, i) * power(parameter.y, j) * power(parameter.z, k)
                    / factorial(i) / factorial(j) / factorial(k);
            result += PN_TRIANGLE_POSITION[ctrlPointIndex ++] * n;
        }
    }
    return result;
}

// 根据 parameter 获得PNTriangle中的法向
vec3 getPNNormal(vec3 parameter) {
    vec3 result = vec3(0);
    int ctrlPointIndex = 0;
    for (int i = 2; i >=0; --i) {
        for (int j = 2 - i; j >= 0; --j) {
            int k = 2 - i - j;
            float n = 2.0 / factorial(i) / factorial(j) / factorial(k)
                * power(parameter.x, i) * power(parameter.y, j) * power(parameter.z, k);
            result += PN_TRIANGLE_NORMAL[ctrlPointIndex ++] * n;
        }
    }
    return normalize(result);
}
void main() {
    TRIANGLE_NO = int(gl_GlobalInvocationID.x);
    if (TRIANGLE_NO >= BUFFER_INPUT_TRIANGLES.length()) {
        return;
    }
    //init grobal var
    ivec3 currentPointsIndex = BUFFER_INPUT_TRIANGLES[TRIANGLE_NO].pointIndex;
    ivec3 currentAdjacentInfo = BUFFER_INPUT_TRIANGLES[TRIANGLE_NO].adjacentInfo;
    for (int i = 0; i < 3; ++i) {
        if (currentAdjacentInfo[i] != -1) {
            ADJACENCY_TRIANGLE_INDEX[i] = int(currentAdjacentInfo[i] >> 2);
            ADJACENCY_TRIANGLE_EDGE[i] = int(currentAdjacentInfo[i] & 3);
        }
        POSITION[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].position;
        NORMAL[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].normal;
    }

    genPNTriangle();

    int pointStart, pointEnd, subTriangleStart, subTriangleEnd;
    getSplitePattern(pointStart, pointEnd, subTriangleStart, subTriangleEnd);
    int pointIndexes[256];
    for (int i = pointStart; i < pointEnd; ++i) {
        int splitPointNo = int(atomicCounterIncrement(ATOMIC_POINT_COUNTER));
        vec3 parameter = changeParameter(BUFFER_SPLIT_PARAMETER[BUFFER_SPLIT_POINT_INDEX[i]]);
        BUFFER_OUTPUT_POINTS[splitPointNo].pn_position = getPNPosition(parameter);
        BUFFER_OUTPUT_POINTS[splitPointNo].pn_normal = getPNNormal(parameter);
        pointIndexes[i - pointStart] = splitPointNo;
    }

    for (int i = subTriangleStart; i < subTriangleEnd; ++i) {
        int splitTriangleNo = int(atomicCounterIncrement(ATOMIC_TRIANGLE_COUNTER));
        ivec3 index = BUFFER_SPLIT_TRIANGLE_INDEX[i];
        for (int j = 0; j < 3; ++j) {
            BUFFER_OUTPUT_TRIANGLES[splitTriangleNo].pointIndex[j] = pointIndexes[index[j]];
        }
    }
    return;
}
