struct RendererPoint {
    vec3 position;
    float texU;
    vec3 normal;
    float texV;
};

layout(std430, binding=1) buffer InputBuffer{
    RendererPoint[] BUFFER_INPUT_POINTS;
};

layout(std430, binding=2) buffer InputBuffer1{
    uint[] BUFFER_INPUT_TRIANGLES;
};

layout(std430, binding=6) buffer InputBuffer2{
    vec3[] BUFFER_INPUT_ORIGINAL_PARAMETER;
};

//output
layout(std430, binding=7) buffer SelectResult{
    vec4[] BUFFER_OUTPUT_SELECTED_POINT;
};

layout(location=0) uniform uint triangleNumber;
layout(location=1) uniform vec3 startPoint;
layout(location=2) uniform vec3 direction;

layout(binding = 1) uniform atomic_uint counter;

//表示group size,这个问题中group size与具体问题无关，先取512,后面再调优
layout(local_size_x = LOCAL_SIZE_X, local_size_y = 1, local_size_z = 1) in;

void main() {
    uint triangleIndex = gl_GlobalInvocationID.x;
    if (triangleIndex >= triangleNumber) {
        return;
    }
    vec3 position[3];
    position[0] = BUFFER_INPUT_POINTS[BUFFER_INPUT_TRIANGLES[triangleIndex * 3u]].position;
    position[1] = BUFFER_INPUT_POINTS[BUFFER_INPUT_TRIANGLES[triangleIndex * 3u + 1u]].position;
    position[2] = BUFFER_INPUT_POINTS[BUFFER_INPUT_TRIANGLES[triangleIndex * 3u + 2u]].position;
    vec3 E1 = position[1] - position[0];
    vec3 E2 = position[2] - position[0];
    mat3 m = mat3(-direction, E1, E2);
    float D = determinant(m);
    // Singular matrix, problem...
    if(abs(D) > 0.000001) {
        mat3 im = inverse(m);
        vec3 T = startPoint - position[0];
        vec3 tvw = im * T;
        if (tvw[0] < 0.0 || tvw[1] > 1.0 || tvw[1] < 0.0 || tvw[2] > 1.0 || tvw[2] < 0.0) {
            return;
        } else {
            vec3 uvw = vec3(1.0 - tvw[1] - tvw[2], tvw[1], tvw[2]);
            vec3 res = vec3(0);
            for (uint i = 0u; i < 3u; ++i) {
                res += BUFFER_INPUT_ORIGINAL_PARAMETER[BUFFER_INPUT_TRIANGLES[triangleIndex * 3u + i]].xyz * uvw[i];
            }
            uint i = atomicCounterIncrement(counter);
            BUFFER_OUTPUT_SELECTED_POINT[i].xyz = res;
            BUFFER_OUTPUT_SELECTED_POINT[i].w = tvw[0];
        }
    }
}
