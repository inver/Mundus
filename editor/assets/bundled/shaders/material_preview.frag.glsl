#version 330 core

#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_diffuseColor;
uniform vec4 lightColor;
uniform vec3 u_camPos;
uniform vec3 u_sunLightPos;

varying vec3 normal;

void main() {
    float specularStrength = 0.5;

    //    vec3 viewDir = normalize(u_camPos - );

    float ambientStrength = 0.8;
    vec4 ambient = ambientStrength * lightColor;

    vec4 result = ambient * u_diffuseColor;
    gl_FragColor = result;
}
