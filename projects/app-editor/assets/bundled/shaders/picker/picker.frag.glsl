#ifdef GL_ES
precision mediump float;
#endif

uniform vec4 u_color;

void main(void) {
    //    gl_FragColor = vec4(0, 0, 0, 1.0);
    gl_FragColor = u_color;
    //    gl_FragColor = vec4(u_color.r/255.0, u_color.g/255.0, u_color.b/255.0, 1.0);
}