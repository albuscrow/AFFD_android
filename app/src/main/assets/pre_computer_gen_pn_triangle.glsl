#version 310 es
struct Point {
    vec4 attr1;
    vec4 attr2;
};

struct InputTriangle {
    ivec4 pointIndex;
    ivec4 adjacentInfo;
};

struct PNTriangle {
    vec3 normalControlPoint[6];
    vec3 positionControlPoint[10];
};

layout(std430, binding=0) buffer InputBuffer{
    Point BUFFER_INPUT_POINTS[];
    InputTriangle BUFFER_INPUT_TRIANGLES[];
};

layout(std430, binding=4) buffer PN_TRIANGLE{
    PNTriangle[] BUFFER_OUTPUT_PN_TRIANGLE;
};

layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

//global data
vec3 POSITION[3];
vec3 NORMAL[3];
ivec3 ADJACENCY_TRIANGLE_INDEX = ivec3(-1);
ivec3 ADJACENCY_TRIANGLE_EDGE = ivec3(-1);
vec3 PN_TRIANGLE_POSITION[10];
vec3 PN_TRIANGLE_NORMAL[6];

const float ZERO = 0.000001;
const vec3 ZERO3 = vec3(ZERO);
int TRIANGLE_NO;

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

    for (int i = 0; i < 10; ++i) {
        BUFFER_OUTPUT_PN_TRIANGLE[TRIANGLE_NO].positionControlPoint[i] = PN_TRIANGLE_POSITION[i];
    }

    for (int i = 0; i < 6; ++i) {
        BUFFER_OUTPUT_PN_TRIANGLE[TRIANGLE_NO].normalControlPoint[i] = PN_TRIANGLE_NORMAL[i];
    }
    return;
}
