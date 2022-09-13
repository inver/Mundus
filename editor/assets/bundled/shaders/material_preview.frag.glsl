#version 330 core

#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_diffuseColor;

void main() {
    gl_FragColor = u_diffuseColor;
}
