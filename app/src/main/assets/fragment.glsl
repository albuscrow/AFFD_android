#version 310 es
precision mediump float;
in vec3 varyingNormal;
in vec2 varyingTexCoord;
out vec4 color;

void main() {
    vec3 lightVector = normalize(vec3(0, 0, 1));
//    vec3 lightVector2 = normalize(vec3(-1, 0, -1));
    float diffuse = max(dot(lightVector, normalize(varyingNormal)), 0.0f);
//    float diffuse2 = max(dot(lightVector2, normalize(varyingNormal)), 0.0f);
//    vec3 temp_color = vec3(diffuse + diffuse2);
    vec3 temp_color = vec3(diffuse);
    color = vec4(temp_color, 1);
}
