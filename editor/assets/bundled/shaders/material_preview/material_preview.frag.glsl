#version 330 core

#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_diffuseColor;
uniform vec4 lightColor;
uniform vec3 u_camPos;
uniform vec3 u_sunLightPos;

varying vec3 normal;
varying vec3 fragPos;

void main() {
    float ambientStrength = 0.8;
    vec4 ambient = ambientStrength * lightColor;

    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(u_sunLightPos - fragPos);

    float diff = max(dot(norm, lightDir), 0.0);
    vec4 diffuse = diff * lightColor;

    gl_FragColor = (ambient + diffuse) * u_diffuseColor;
}
