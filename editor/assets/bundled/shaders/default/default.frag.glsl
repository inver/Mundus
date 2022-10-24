//#ifdef позволяет коду работать на слабых телефонах, и мощных пк.Если шейдер используется на телефоне(GL_ES) то
//используется низкая разрядность (точность) данных.(
//highp – высокая точность;
//mediump – средняя точность;
//lowp – низкая точность)

#ifdef GL_ES
    #define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;

// sampler2D это специальный формат данных в  glsl для доступа к текстуре

uniform sampler2D u_texture;

void main(){
    gl_FragColor = v_color;// * texture2D(u_texture, v_texCoords);// итоговый цвет пикселя
}