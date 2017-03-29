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

layout(std140, binding=2) uniform TessellatedInfo{
    uniform vec3[66] TESSELLATION_PARAMETER;
    uniform uvec3[100] TESSELLATION_INDEX;
    uniform uint TESSELLATION_LEVEL;
};

//struct SplitTriangle {
//    uvec3 pointIndex;
//    vec4 adjacentPNNormal[6];
//};
//
struct SplitPoint {
    vec3 pnPosition;
    float texU;
    vec3 pnNormal;
    float texV;
    vec3 originalPosition;
    uint cageIndex;
};

layout(std430, binding=5) buffer SplitTriangleBuffer{
    vec4 adjacentPNNormal[SPLITTED_TRIANGLE_NUMBER * 6];
    uvec3 pointIndex[SPLITTED_TRIANGLE_NUMBER];

    vec3 pnPosition[SPLITTED_POINT_NUMBER];
    vec3 pnNormal[SPLITTED_POINT_NUMBER];
    vec3 originalPosition[SPLITTED_POINT_NUMBER];
    vec2 actex[SPLITTED_POINT_NUMBER];
    uint cageIndex[SPLITTED_POINT_NUMBER];
};

struct RendererPoint {
    vec3 position;
    float texU;
    vec3 normal;
    float texV;
};

struct SampledPoint {
    vec3 position;
    vec3 normal;
};

layout(std430, binding=1) buffer OutputBuffer0{
    RendererPoint[] BUFFER_OUTPUT_POINTS;
};

layout(std430, binding=2) buffer OutputBuffer1{
    uint[] BUFFER_OUTPUT_TRIANGLES;
};

layout(std430, binding=6) buffer OutputBuffer2{
    vec3[] BUFFER_OUTPUT_ORIGINAL_PARAMETER;
};

layout(std430, binding=16) buffer DebugBuffer{
    vec4[] BUFFER_DEBUG_OUTPUT;
};

uint TRIANGLE_NO;
const uint SPLIT_TRIANGLE_NUMBER = SPLIT_TRIANGLE_NUMBER_M;
layout(local_size_x = LOCAL_SIZE_X, local_size_y = 1, local_size_z = 1) in;


struct SamplePoint {
    vec3 parameter;
    vec3 normal;
    uvec3 cageIndex;
};

//global const data
vec3 SAMPLE_PARAMETER[19] = vec3[19](vec3(1.0 , 0.0 , 0.0),
    vec3(0.6666666666666666 , 0.3333333333333333 , 0.0),
    vec3(0.6666666666666666 , 0.0 , 0.3333333333333333),
    vec3(0.3333333333333333 , 0.6666666666666666 , 0.0),
    vec3(0.3333333333333333 , 0.0 , 0.6666666666666666),
    vec3(0.0 , 1.0 , 0.0),
    vec3(0.0 , 0.6666666666666666 , 0.3333333333333333),
    vec3(0.0 , 0.3333333333333333 , 0.6666666666666666),
    vec3(0.0 , 0.0 , 1.0),

    vec3(0.6666666666666666 , 0.16666666666666666 , 0.16666666666666666),
    vec3(0.5 , 0.3333333333333333 , 0.16666666666666666),
    vec3(0.5 , 0.16666666666666666 , 0.3333333333333333),
    vec3(0.3333333333333333 , 0.5 , 0.16666666666666666),
    vec3(0.3333333333333333 , 0.3333333333333333 , 0.3333333333333333),
    vec3(0.3333333333333333 , 0.16666666666666666 , 0.5),
    vec3(0.16666666666666666 , 0.6666666666666666 , 0.16666666666666666),
    vec3(0.16666666666666666 , 0.5 , 0.3333333333333333),
    vec3(0.16666666666666666 , 0.3333333333333333 , 0.5),
    vec3(0.16666666666666666 , 0.16666666666666666 , 0.6666666666666666)
);

float Mr[54] = float[54](
      -0.8333333,        3.0000000,         0.0000000,        -1.5000000,         0.0000000,         0.3333333,        0.0000000,        0.0000000,        0.0000000,
      -0.8333333,        0.0000000,         3.0000000,         0.0000000,        -1.5000000,         0.0000000,        0.0000000,        0.0000000,        0.3333333,
       0.3333333,       -1.5000000,         0.0000000,         3.0000000,         0.0000000,        -0.8333333,        0.0000000,        0.0000000,        0.0000000,
       0.3333333,        0.0000000,        -1.5000000,         0.0000000,         3.0000000,         0.0000000,        0.0000000,        0.0000000,       -0.8333333,
       0.0000000,        0.0000000,         0.0000000,         0.0000000,         0.0000000,        -0.8333333,        3.0000000,       -1.5000000,        0.3333333,
       0.0000000,        0.0000000,         0.0000000,         0.0000000,         0.0000000,         0.3333333,       -1.5000000,        3.0000000,       -0.8333333
);

float Mr_4[19] = float[19](
0.2784553,
-0.9969512,
-0.9969512,
-0.9969512,
-0.9969512,
0.2784553,
-0.9969512,
-0.9969512,
0.2784553,

0.4390244,
0.6585366,
0.6585366,
0.6585366,
0.8780488,
0.6585366,
0.4390244,
0.6585366,
0.6585366,
0.4390244
);

float rfactorialt[10] = float[10](1.0,
    3.0, 3.0,
    3.0, 6.0, 3.0,
    1.0, 3.0, 3.0, 1.0);

//global data
SplitPoint SPLIT_POINTS[3];
SamplePoint getSamplePoint(vec3 parameter) {
    SamplePoint result;
    result.parameter = vec3(0);
    for (int i = 0; i < 3; ++i) {
        result.parameter += SPLIT_POINTS[i].pnPosition * parameter[i];
    }

    result.parameter = (result.parameter - BSPLINEBODY_START_POINT) / BSPLINEBODY_STEP;
    result.cageIndex = uvec3(result.parameter);
    result.parameter = result.parameter - floor(result.parameter);

    result.normal = vec3(0);
    for (int i = 0; i < 3; ++i) {
        result.normal += SPLIT_POINTS[i].pnNormal * parameter[i];
    }
    normalize(result.normal);
    return result;
}

vec3 sampleHelper(const uvec3 knot_left_index, const vec3 un, const vec3 vn, const vec3 wn){
    uint controlPointOffset = knot_left_index.x * BSPLINEBODY_INTERVAL_NUM[1] + knot_left_index.y;
    controlPointOffset = controlPointOffset * BSPLINEBODY_INTERVAL_NUM[2] + knot_left_index.z;
    controlPointOffset = controlPointOffset * BSPLINEBODY_ORDER_PRODUCT - 1u;
    vec3 tempcp2[3][3];
    for (int i = 0; i < 3; ++i){
        for (int j = 0; j < 3; ++j){
            tempcp2[i][j] = UNIFORM_CONTROL_POINT[++controlPointOffset].xyz * wn[0];
            for (int k = 1; k < 3; ++k) {
                tempcp2[i][j] += UNIFORM_CONTROL_POINT[++controlPointOffset].xyz * wn[k];
            }
        }
    }
    vec3 tempcp1[3];
    for (int i = 0; i < 3; ++i) {
        tempcp1[i] = tempcp2[i][0] * vn[0];
        for (int j = 1; j < 3; ++j) {
            tempcp1[i] += tempcp2[i][j] * vn[j];
        }
    }
    return tempcp1[0] * un[0] + tempcp1[1] * un[1] + tempcp1[2] * un[2];
}

vec3 sampleFastNormal(in SamplePoint samplePoint) {
    float u = samplePoint.parameter.x;
    float v = samplePoint.parameter.y;
    float w = samplePoint.parameter.z;

    vec3 un  = vec3(1, u, u * u);
    vec3 vn  = vec3(1, v, v * v);
    vec3 wn  = vec3(1, w, w * w);
    vec3 un_ = vec3(0, 1, 2.0 * u);
    vec3 vn_ = vec3(0, 1, 2.0 * v);
    vec3 wn_ = vec3(0, 1, 2.0 * w);

    vec3 fu = sampleHelper(samplePoint.cageIndex, un_, vn, wn);
    vec3 fv = sampleHelper(samplePoint.cageIndex, un, vn_, wn);
    vec3 fw = sampleHelper(samplePoint.cageIndex, un, vn, wn_);

    vec3 n = samplePoint.normal;
    vec3 res;
    // J_bar_star_T_[012]表示J_bar的伴随矩阵的转置(即J_bar*T)的第一行三个元素
    float J_bar_star_T_0 = fv.y * fw.z - fw.y * fv.z;
    float J_bar_star_T_1 = fw.y * fu.z - fu.y * fw.z;
    float J_bar_star_T_2 = fu.y * fv.z - fv.y * fu.z;
    res.x = n.x * J_bar_star_T_0 * BSPLINEBODY_STEP[0] + n.y * J_bar_star_T_1 * BSPLINEBODY_STEP[1] + n.z * J_bar_star_T_2 * BSPLINEBODY_STEP[2];

    // J_bar_star_T_[012]表示J_bar的伴随矩阵的转置(即J_bar*T)的第二行三个元素
    J_bar_star_T_0 = fv.z * fw.x - fw.z * fv.x;
    J_bar_star_T_1 = fw.z * fu.x - fu.z * fw.x;
    J_bar_star_T_2 = fu.z * fv.x - fv.z * fu.x;
    res.y = n.x * J_bar_star_T_0 * BSPLINEBODY_STEP[0] + n.y * J_bar_star_T_1 * BSPLINEBODY_STEP[1] + n.z * J_bar_star_T_2 * BSPLINEBODY_STEP[2];

    // J_bar_star_T_[012]表示J_bar的伴随矩阵的转置(即J_bar*T)的第三行三个元素
    J_bar_star_T_0 = fv.x * fw.y - fw.x * fv.y;
    J_bar_star_T_1 = fw.x * fu.y - fu.x * fw.y;
    J_bar_star_T_2 = fu.x * fv.y - fv.x * fu.y;
    res.z = n.x * J_bar_star_T_0 * BSPLINEBODY_STEP[0] + n.y * J_bar_star_T_1 * BSPLINEBODY_STEP[1] + n.z * J_bar_star_T_2 * BSPLINEBODY_STEP[2];
    return normalize(res);
}


SampledPoint sampleFast(in SamplePoint samplePoint) {
    float u = samplePoint.parameter.x;
    float v = samplePoint.parameter.y;
    float w = samplePoint.parameter.z;

    vec3 un  = vec3(1, u, u * u);
    vec3 vn  = vec3(1, v, v * v);
    vec3 wn  = vec3(1, w, w * w);
    vec3 un_ = vec3(0, 1, 2.0 * u);
    vec3 vn_ = vec3(0, 1, 2.0 * v);
    vec3 wn_ = vec3(0, 1, 2.0 * w);

    SampledPoint res;
    res.position =  sampleHelper(samplePoint.cageIndex, un, vn, wn);
    vec3 fu = sampleHelper(samplePoint.cageIndex, un_, vn, wn);
    vec3 fv = sampleHelper(samplePoint.cageIndex, un, vn_, wn);
    vec3 fw = sampleHelper(samplePoint.cageIndex, un, vn, wn_);

    vec3 n = samplePoint.normal;
    // J_bar_star_T_[012]表示J_bar的伴随矩阵的转置(即J_bar*T)的第一行三个元素
    float J_bar_star_T_0 = fv.y * fw.z - fw.y * fv.z;
    float J_bar_star_T_1 = fw.y * fu.z - fu.y * fw.z;
    float J_bar_star_T_2 = fu.y * fv.z - fv.y * fu.z;
    res.normal.x = n.x * J_bar_star_T_0 * BSPLINEBODY_STEP[0] + n.y * J_bar_star_T_1 * BSPLINEBODY_STEP[1] + n.z * J_bar_star_T_2 * BSPLINEBODY_STEP[2];

    // J_bar_star_T_[012]表示J_bar的伴随矩阵的转置(即J_bar*T)的第二行三个元素
    J_bar_star_T_0 = fv.z * fw.x - fw.z * fv.x;
    J_bar_star_T_1 = fw.z * fu.x - fu.z * fw.x;
    J_bar_star_T_2 = fu.z * fv.x - fv.z * fu.x;
    res.normal.y = n.x * J_bar_star_T_0 * BSPLINEBODY_STEP[0] + n.y * J_bar_star_T_1 * BSPLINEBODY_STEP[1] + n.z * J_bar_star_T_2 * BSPLINEBODY_STEP[2];

    // J_bar_star_T_[012]表示J_bar的伴随矩阵的转置(即J_bar*T)的第三行三个元素
    J_bar_star_T_0 = fv.x * fw.y - fw.x * fv.y;
    J_bar_star_T_1 = fw.x * fu.y - fu.x * fw.y;
    J_bar_star_T_2 = fu.x * fv.y - fv.x * fu.y;
    res.normal.z = n.x * J_bar_star_T_0 * BSPLINEBODY_STEP[0] + n.y * J_bar_star_T_1 * BSPLINEBODY_STEP[1] + n.z * J_bar_star_T_2 * BSPLINEBODY_STEP[2];
    normalize(res.normal);
    return res;
}

float power(float b, int n) {
    if (n == 0) {
        return 1.0;
    } else if (b < 0.00001) {
        return 0.0;
    } else {
        return pow(b, float(n));
    }
}

vec3 getOrigianlParameter(vec3 tessellationParameter) {
    vec3 res = vec3(0);
    for (int i = 0; i < 3; ++i) {
        res += SPLIT_POINTS[i].originalPosition * tessellationParameter[i];
    }
    return res;
}

void main() {
    TRIANGLE_NO = uint(gl_GlobalInvocationID.x);
    if (TRIANGLE_NO >= SPLIT_TRIANGLE_NUMBER) {
        return;
    }
    //init grobal var
    uvec3 currentPointsIndex = pointIndex[TRIANGLE_NO];
    for (int i = 0; i < 3; ++i) {
        SPLIT_POINTS[i].cageIndex = cageIndex[currentPointsIndex[i]];
        SPLIT_POINTS[i].originalPosition = originalPosition[currentPointsIndex[i]];
        SPLIT_POINTS[i].pnNormal = pnNormal[currentPointsIndex[i]];
        SPLIT_POINTS[i].pnPosition = pnPosition[currentPointsIndex[i]];
        SPLIT_POINTS[i].texU = actex[currentPointsIndex[i]].x;
        SPLIT_POINTS[i].texV = actex[currentPointsIndex[i]].y;
//        uint temp = uint(int(TRIANGLE_NO) * 3 + i - (int(TRIANGLE_NO) * 3 + i) / 10);
//        BUFFER_DEBUG_OUTPUT[temp] = vec4(cageIndex[currentPointsIndex[i]], 1024, 10010, 10086);
    }

    SamplePoint samplePoints[19];
    for (int i = 0; i < 19; ++i) {
        samplePoints[i] = getSamplePoint(SAMPLE_PARAMETER[i]);
    }

    SampledPoint sampledPoints[19];
    for (int i = 0; i < 19; ++i) {
        sampledPoints[i] = sampleFast(samplePoints[i]);
    }


    // 计算Bezier曲面片控制顶点
    vec3 POSITION_CONTROL_POINT[10];
    vec4 NORMAL_CONTROL_POINT[10];

    POSITION_CONTROL_POINT[0] = sampledPoints[0].position;
    NORMAL_CONTROL_POINT[0].xyz = sampledPoints[0].normal;
    POSITION_CONTROL_POINT[6] = sampledPoints[5].position;
    NORMAL_CONTROL_POINT[6].xyz = sampledPoints[5].normal;
    POSITION_CONTROL_POINT[9] = sampledPoints[8].position;
    NORMAL_CONTROL_POINT[9].xyz = sampledPoints[8].normal;

    int tempindex = -1;
    int aux1[6] = int[6](1,2,3,5,7,8);
    for (int i = 0; i < 6; ++i) {
        POSITION_CONTROL_POINT[aux1[i]] = vec3(0);
        NORMAL_CONTROL_POINT[aux1[i]].xyz = vec3(0);
        for (int j = 0; j < 9; ++j) {
            POSITION_CONTROL_POINT[aux1[i]] += sampledPoints[j].position * Mr[++tempindex];
            NORMAL_CONTROL_POINT[aux1[i]].xyz += sampledPoints[j].normal * Mr[tempindex];
        }
    }
    POSITION_CONTROL_POINT[4] = vec3(0);
    NORMAL_CONTROL_POINT[4].xyz = vec3(0);
    for (int j = 0; j < 19; ++j) {
        POSITION_CONTROL_POINT[4] += sampledPoints[j].position * Mr_4[j];
        NORMAL_CONTROL_POINT[4].xyz += sampledPoints[j].normal * Mr_4[j];
    }


    uint moveControlPoint[6] = uint[6](2u,1u,3u,7u,8u,5u);
    ivec3 vertexIndexInSamplePoint = ivec3(0,5,8);
    vec3 E = vec3(0);
    for (int i = 0; i < 6; ++i) {
        vec3 currentNormal = sampledPoints[vertexIndexInSamplePoint[i >> 1]].normal;
        vec3 currentPosition = sampledPoints[vertexIndexInSamplePoint[i >> 1]].position;
        vec3 controlPoint = POSITION_CONTROL_POINT[moveControlPoint[i]];
        vec4 adjancyNormal = adjacentPNNormal[TRIANGLE_NO * 6u + uint(i)];
        if (adjancyNormal[3] > -1.0) {
            SamplePoint samplePointForNormal = samplePoints[vertexIndexInSamplePoint[i >> 1]];
            samplePointForNormal.normal = adjancyNormal.xyz;
            vec3 n_ave = normalize(cross(currentNormal, sampleFastNormal(samplePointForNormal)));
            POSITION_CONTROL_POINT[moveControlPoint[i]] = currentPosition + dot(controlPoint - currentPosition, n_ave) * n_ave;;
        } else {
            POSITION_CONTROL_POINT[moveControlPoint[i]] = controlPoint - dot(controlPoint - currentPosition, currentNormal) * currentNormal;
        }
        E += POSITION_CONTROL_POINT[moveControlPoint[i]];
    }
    E *= 0.25;
    POSITION_CONTROL_POINT[4] = (POSITION_CONTROL_POINT[0] + POSITION_CONTROL_POINT[6] + POSITION_CONTROL_POINT[9]) * -0.166666666 + E;


    // tessellation
    uint pointNumber = (TESSELLATION_LEVEL + 1u) * (TESSELLATION_LEVEL + 2u) / 2u;
    uint triangleNumber = TESSELLATION_LEVEL * TESSELLATION_LEVEL;

    uint pointOffset = TRIANGLE_NO * pointNumber;
    uint pointIndex[66];
    for (uint i = 0u; i < pointNumber; ++i) {
        vec3 parameter = TESSELLATION_PARAMETER[i];

        int ctrlPointIndex = 0;
        vec3 position = vec3(0);
        vec3 normal = vec3(0);
        for (int i = 3; i >=0; --i) {
            for (int j = 3 - i; j >= 0; --j) {
                int k = 3 - i - j;
                float t = rfactorialt[ctrlPointIndex] * power(parameter.x, i) * power(parameter.y, j) * power(parameter.z, k);
                position += POSITION_CONTROL_POINT[ctrlPointIndex] * t;;
                normal += NORMAL_CONTROL_POINT[ctrlPointIndex].xyz * t;
                ++ ctrlPointIndex;
            }
        }
        normal = normalize(normal);
        BUFFER_OUTPUT_POINTS[pointOffset].position = position;
        BUFFER_OUTPUT_POINTS[pointOffset].normal = normal;

        float tempTexU = 0.0f;
        float tempTexV = 0.0f;
        for (int j = 0; j < 3; ++j) {
            tempTexU += SPLIT_POINTS[j].texU * parameter[j];
            tempTexV += SPLIT_POINTS[j].texV * parameter[j];
        }
        BUFFER_OUTPUT_POINTS[pointOffset].texU = tempTexU;
        BUFFER_OUTPUT_POINTS[pointOffset].texV = tempTexV;


        BUFFER_OUTPUT_ORIGINAL_PARAMETER[pointOffset] = getOrigianlParameter(parameter);
        pointIndex[i] = pointOffset;
        ++ pointOffset;
    }

    uint triangleOffset = TRIANGLE_NO * triangleNumber * 3u;
    for (uint i = 0u; i < triangleNumber; ++i) {
        BUFFER_OUTPUT_TRIANGLES[triangleOffset++] = pointIndex[TESSELLATION_INDEX[i].x];
        BUFFER_OUTPUT_TRIANGLES[triangleOffset++] = pointIndex[TESSELLATION_INDEX[i].y];
        BUFFER_OUTPUT_TRIANGLES[triangleOffset++] = pointIndex[TESSELLATION_INDEX[i].z];
    }
    return;
}
