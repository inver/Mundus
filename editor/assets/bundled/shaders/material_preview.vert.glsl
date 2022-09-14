#version 330 core

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;

varying vec3 normal;
//varying vec3

void main() {
    gl_Position = u_projViewMatrix * u_transMatrix * vec4(a_position, 1.0);
}
