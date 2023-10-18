attribute vec3 a_position;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform float time = 20.0f;

varying vec3 v_position;
varying vec3 f_sun;

void main() {
    vec4 pos = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);

    v_position = a_position;
    gl_Position = pos.xyww;

    f_sun = vec3(0.0, sin(time * 0.01), cos(time * 0.01));
}