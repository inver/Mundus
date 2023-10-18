attribute vec3 a_position;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;

varying vec3 v_position;

void main() {
    vec4 pos = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);

    v_position = a_position;
    gl_Position = pos.xyww;
}