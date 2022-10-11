//#version 330 core

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;
uniform mat4 u_modelMatrix;

varying vec3 normal;
varying vec3 fragPos;

void main() {
    gl_Position = u_projViewMatrix * u_transMatrix * vec4(a_position, 1.0);
    //    gl_Position = u_projViewMatrix * u_transMatrix * u_modelMatrix * vec4(a_position, 1.0);
    fragPos = a_position;
    //    fragPos = vec3(u_modelMatrix * vec4(a_position, 1.0));
    normal = a_normal;
}
