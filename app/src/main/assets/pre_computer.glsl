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
    vec3 ijk;
    ijk[0] = min(l01, min(l12, l20));
    ijk[2] = max(l01, max(l12, l20));
    ijk[1] = (l01 + l12 + l20) - ijk[0] - ijk[2];
    ivec3 ijk_int;
    for (int i = 0; i < 3; ++i) {
        ijk_int[i] = int(ceil(ijk[i] / CONST_SPLIT_FACTOR));
    }

    int offset = getOffset(ijk_int[0], ijk_int[1], ijk_int[2]);
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

vec3 getAdjacencyNormal(uint adjacency_index, bool isFirst, vec3 normal) {
    int triangleIndex = ADJACENCY_TRIANGLE_INDEX[adjacency_index];
    if (triangleIndex == -1) {
        return normal;
    }
    int pointIndex = ADJACENCY_TRIANGLE_EDGE[adjacency_index];
    if (isFirst) {
        if (pointIndex == 0) {
            pointIndex = 3;
        }
        pointIndex -= 1;
    }
    return BUFFER_INPUT_POINTS[BUFFER_INPUT_TRIANGLES[triangleIndex].pointIndex[pointIndex]].attr2.xyz;
}

vec3 genPNControlPoint(vec3 p_s, vec3 p_e, vec3 n, vec3 n_adj) {
    if (all(lessThan(abs(n - n_adj), ZERO3))) {
        return (p_s * 2.0 + p_e - dot((p_e - p_s), n) * n) / 3.0;
    } else {
        vec3 T = cross(n, n_adj);
        return p_s + dot((p_e - p_s), T) / 3.0 * T;
    }
}

vec3 genPNControlNormal(vec3 p_s, vec3 p_e, vec3 n_s, vec3 n_e) {
    vec3 n = n_s + n_e;
    vec3 v = p_e - p_s;
    if (all(lessThan(abs(v), ZERO3))) {
        return normalize(n);
    } else {
        return normalize(n - 2.0 * dot(v, n) / dot(v, v) * v);
    }
}

void genPNTriangle(){
    // 三个顶点对应的控制顶点
    PN_TRIANGLE_POSITION[0] = POSITION[0];
    PN_TRIANGLE_POSITION[6] = POSITION[1];
    PN_TRIANGLE_POSITION[9] = POSITION[2];

    //邻接三角形的六个法向nij,i表示近顶点编号,j远顶点编号
    vec3 n02, n01, n10, n12, n21, n20;
    n02 = getAdjacencyNormal(0u, true,  NORMAL[0]);
    n01 = getAdjacencyNormal(1u, false, NORMAL[0]);
    n10 = getAdjacencyNormal(1u, true,  NORMAL[1]);
    n12 = getAdjacencyNormal(2u, false, NORMAL[1]);
    n21 = getAdjacencyNormal(2u, true,  NORMAL[2]);
    n20 = getAdjacencyNormal(0u, false, NORMAL[2]);


    //two control point near p0
    PN_TRIANGLE_POSITION[2] = genPNControlPoint(POSITION[0], POSITION[2], NORMAL[0], n02);
    PN_TRIANGLE_POSITION[1] = genPNControlPoint(POSITION[0], POSITION[1], NORMAL[0], n01);
    //two control POSITION near p1
    PN_TRIANGLE_POSITION[3] = genPNControlPoint(POSITION[1], POSITION[0], NORMAL[1], n10);
    PN_TRIANGLE_POSITION[7] = genPNControlPoint(POSITION[1], POSITION[2], NORMAL[1], n12);
    //two control POSITION near p2
    PN_TRIANGLE_POSITION[8] = genPNControlPoint(POSITION[2], POSITION[1], NORMAL[2], n21);
    PN_TRIANGLE_POSITION[5] = genPNControlPoint(POSITION[2], POSITION[0], NORMAL[2], n20);

    vec3 E = (PN_TRIANGLE_POSITION[1] + PN_TRIANGLE_POSITION[2] + PN_TRIANGLE_POSITION[3]
    + PN_TRIANGLE_POSITION[5] + PN_TRIANGLE_POSITION[7] + PN_TRIANGLE_POSITION[8]) / 6.0;
    vec3 V = (POSITION[0] + POSITION[1] + POSITION[2]) / 3.0;
    PN_TRIANGLE_POSITION[4] = E + (E - V) / 2.0;

    // 生成法向PN-triangle
    PN_TRIANGLE_NORMAL[0] = NORMAL[0];
    PN_TRIANGLE_NORMAL[3] = NORMAL[1];
    PN_TRIANGLE_NORMAL[5] = NORMAL[2];

    PN_TRIANGLE_NORMAL[1] = genPNControlNormal(POSITION[0], POSITION[1], NORMAL[0], NORMAL[1]);
    PN_TRIANGLE_NORMAL[4] = genPNControlNormal(POSITION[1], POSITION[2], NORMAL[1], NORMAL[2]);
    PN_TRIANGLE_NORMAL[2] = genPNControlNormal(POSITION[2], POSITION[0], NORMAL[2], NORMAL[0]);
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
    ivec4 currentPointsIndex = BUFFER_INPUT_TRIANGLES[TRIANGLE_NO].pointIndex;
    ivec4 currentAdjacentInfo = BUFFER_INPUT_TRIANGLES[TRIANGLE_NO].adjacentInfo;
    for (int i = 0; i < 3; ++i) {
        if (currentAdjacentInfo[i] != -1) {
            ADJACENCY_TRIANGLE_INDEX[i] = int(currentAdjacentInfo[i] >> 2);
            ADJACENCY_TRIANGLE_EDGE[i] = int(currentAdjacentInfo[i] & 3);
        }

        POSITION[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].attr1.xyz;
        NORMAL[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].attr2.xyz;
    }

    genPNTriangle();

    int splitIndexOffset, subTriangleNumber;
    getSplitePattern(splitIndexOffset, subTriangleNumber);
    for (int i = 0; i < subTriangleNumber; ++i) {
        int splitTriangleNo = int(atomicCounterIncrement(ATOMIC_TRIANGLE_COUNTER));
        ivec4 index = BUFFER_SPLIT_INDEX[splitIndexOffset + i];
        for (int j = 0; j < 3; ++j) {
            vec3 parameter = changeParameter(BUFFER_SPLIT_PARAMETER[index[j]].xyz);
            int point_index = splitTriangleNo * 3 + j;
            BUFFER_OUTPUT_POINTS[point_index].attr1.xyz = getPNPosition(parameter);
            BUFFER_OUTPUT_POINTS[point_index].attr2.xyz = getPNNormal(parameter);
            BUFFER_OUTPUT_TRIANGLES[point_index] = point_index;
        }
    }
    return;
}
