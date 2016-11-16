struct InputPoint {
    vec4 p3t1;
    vec4 n3t1;
};

struct InputTriangle {
    ivec3 pointIndex;
    ivec3 adjacentInfo;
};
layout(std430, binding=0) buffer InputBuffer{
    InputPoint BUFFER_INPUT_POINTS[POINT_NUMBER];
    InputTriangle BUFFER_INPUT_TRIANGLES[TRIANGLE_NUMBER];
};

struct PNTriangle {
    vec3 normalControlPoint[6];
    vec3 positionControlPoint[10];
};
layout(std430, binding=4) buffer PN_TRIANGLE{
    PNTriangle[] BUFFER_OUTPUT_PN_TRIANGLE;
};

layout(local_size_x = LOCAL_SIZE_X, local_size_y = 1, local_size_z = 1) in;

//global data
vec3 position[3];
vec3 normal[3];
ivec3 ADJACENCY_TRIANGLE_INDEX = ivec3(-1);
ivec3 ADJACENCY_TRIANGLE_EDGE = ivec3(-1);

const float ZERO = 0.000001;
const vec3 ZERO3 = vec3(ZERO);

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
    return BUFFER_INPUT_POINTS[BUFFER_INPUT_TRIANGLES[triangleIndex].pointIndex[pointIndex]].n3t1.xyz;
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

void main() {
    int triangle_no = int(gl_GlobalInvocationID.x);
    if (triangle_no >= BUFFER_INPUT_TRIANGLES.length()) {
        return;
    }
    //init grobal var
    ivec3 currentPointsIndex = BUFFER_INPUT_TRIANGLES[triangle_no].pointIndex;
    ivec3 currentAdjacentInfo = BUFFER_INPUT_TRIANGLES[triangle_no].adjacentInfo;
    vec3 position[3];
    vec3 normal[3];
    for (int i = 0; i < 3; ++i) {
        if (currentAdjacentInfo[i] != -1) {
            ADJACENCY_TRIANGLE_INDEX[i] = int(currentAdjacentInfo[i] >> 2);
            ADJACENCY_TRIANGLE_EDGE[i] = int(currentAdjacentInfo[i] & 3);
        }
        position[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].p3t1.xyz;
        normal[i] = BUFFER_INPUT_POINTS[currentPointsIndex[i]].n3t1.xyz;
    }
    PNTriangle pnTriangle;
    // 三个顶点对应的控制顶点
    pnTriangle.positionControlPoint[0] = position[0];
    pnTriangle.positionControlPoint[6] = position[1];
    pnTriangle.positionControlPoint[9] = position[2];

    //邻接三角形的六个法向nij,i表示近顶点编号,j远顶点编号
    vec3 n02, n01, n10, n12, n21, n20;
    n02 = getAdjacencyNormal(0u, true,  normal[0]);
    n01 = getAdjacencyNormal(1u, false, normal[0]);
    n10 = getAdjacencyNormal(1u, true,  normal[1]);
    n12 = getAdjacencyNormal(2u, false, normal[1]);
    n21 = getAdjacencyNormal(2u, true,  normal[2]);
    n20 = getAdjacencyNormal(0u, false, normal[2]);


    //two control point near p0
    pnTriangle.positionControlPoint[2] = genPNControlPoint(position[0], position[2], normal[0], n02);
    pnTriangle.positionControlPoint[1] = genPNControlPoint(position[0], position[1], normal[0], n01);
    //two control POSITION near p1
    pnTriangle.positionControlPoint[3] = genPNControlPoint(position[1], position[0], normal[1], n10);
    pnTriangle.positionControlPoint[7] = genPNControlPoint(position[1], position[2], normal[1], n12);
    //two control POSITION near p2
    pnTriangle.positionControlPoint[8] = genPNControlPoint(position[2], position[1], normal[2], n21);
    pnTriangle.positionControlPoint[5] = genPNControlPoint(position[2], position[0], normal[2], n20);

    vec3 E = (pnTriangle.positionControlPoint[1] + pnTriangle.positionControlPoint[2] + pnTriangle.positionControlPoint[3]
    + pnTriangle.positionControlPoint[5] + pnTriangle.positionControlPoint[7] + pnTriangle.positionControlPoint[8]) / 6.0;
    vec3 V = (position[0] + position[1] + position[2]) / 3.0;
    pnTriangle.positionControlPoint[4] = E + (E - V) / 2.0;

    // 生成法向PN-triangle
    pnTriangle.normalControlPoint[0] = normal[0];
    pnTriangle.normalControlPoint[3] = normal[1];
    pnTriangle.normalControlPoint[5] = normal[2];

    pnTriangle.normalControlPoint[1] = genPNControlNormal(position[0], position[1], normal[0], normal[1]);
    pnTriangle.normalControlPoint[4] = genPNControlNormal(position[1], position[2], normal[1], normal[2]);
    pnTriangle.normalControlPoint[2] = genPNControlNormal(position[2], position[0], normal[2], normal[0]);

    BUFFER_OUTPUT_PN_TRIANGLE[triangle_no] = pnTriangle;
}
