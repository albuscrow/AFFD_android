#version 310 es

//precision mediump float;
//
//in vec3 varyingNormal;
//in vec3 varyingPosition;
//in vec2 varyingTexCoord;
//
//out vec4 color;
//
//void main() {
//    vec3 lightVector = normalize(vec3(0, 0, 1));
//    float diffuse = max(dot(lightVector, normalize(varyingNormal)), 0.0f);
//    vec3 temp_color = vec3(diffuse);
//    color = vec4(temp_color, 1);
//}
//
//#version 100
precision mediump float;

uniform sampler2D uTexture;
//uniform samplerCube uCube;

const vec4 uLightPosition = vec4(-20f, 10.0f, 20f, 1.0f);
const vec4 uAmbientL = vec4(0.3f, 0.3f, 0.3f, 1.0f);
const vec4 uDiffuseL = vec4(0.6f, 0.6f, 0.6f, 1.0f);
const vec4 uSpecularL = vec4(0.7f, 0.7f, 0.7f, 1.0f);
const vec4 uLightPosition2 = vec4(20.0f, 5.0f, -9.0f, 1.0f);
const vec4 uAmbientL2 = vec4(0.3f, 0.3f, 0.3f, 1.0f);
const vec4 uDiffuseL2 = vec4(0.75f, 0.9f, 0.75f, 1.0f);
const vec4 uSpecularL2 = vec4(0.0f, 0.0f, 0.0f, 1.0f);
const vec4 uAmbientM = vec4(0.6f, 0.6f, 0.6f, 1.0f);
const vec4 uDiffuseM = vec4(0.8f, 0.8f, 0.6f, 1.0f);
const vec4 uSpecularM = vec4(0.7f, 0.7f, 0.7f, 1.0f);

const float uShininess = 50.0f;

//look at point;
const vec4 uLookAt = vec4(0, 0, 0, 0);

//data from vertex shader
in vec4 varyingPosition;
in vec3 varyingNormal;    //法向座标
in vec2 varyingTexCoord;  //纹理座标

out vec4 color;

void main(){
    //computer ambient;
    vec4 ambient = uAmbientM * uAmbientL;
    vec4 ambient2 = uAmbientM * uAmbientL2;

    //computer diffuse;
    float dis = length(uLightPosition - varyingPosition);
    vec4 lightVector = normalize(uLightPosition - varyingPosition);
    lightVector = normalize(vec4(-1,1,2,0));
    vec4 diffuse = uDiffuseL * uDiffuseM * max(dot(vec3(lightVector), varyingNormal), 0.0);

    vec4 lightVector2 = normalize(uLightPosition2 - varyingPosition);
    lightVector2 = vec4(1.2, 0, -1, 1);
    vec4 diffuse2 = uDiffuseL2 * uDiffuseM * max(dot(vec3(lightVector2), varyingNormal), 0.0);

    //computer specular
    vec4 s = normalize(normalize(uLookAt - varyingPosition) + lightVector);
    float specularFactor = pow(max(dot(vec3(s), varyingNormal), 0.0), uShininess);
    vec4 specular = uSpecularL * uSpecularM * specularFactor;

    vec4 s2 = normalize(normalize(uLookAt - varyingPosition) + lightVector2);
    float specularFactor2 = pow(max(dot(vec3(s2), varyingNormal), 0.0), uShininess);
    vec4 specular2 = uSpecularL2 * uSpecularM * specularFactor2;

    vec3 texCoordCube = reflect(normalize(uLookAt - varyingPosition).xyz, varyingNormal);
    //computer final color
    color =
        texture(uTexture, varyingTexCoord)
        ;
//    	* (diffuse + ambient + diffuse2 + ambient2 )
//    	+ specular + specular2;
        //+ textureCube(uCube,texCoordCube) * 0.01;
//        color = vec4(1,0,0,0);

//    vec3 lightVectort = normalize(vec3(0, 0, 1));
//    float diffuset = max(dot(lightVectort, normalize(varyingNormal)), 0.0f);
//    vec3 temp_color = vec3(diffuset);
//    color = vec4(temp_color, 1);

}
