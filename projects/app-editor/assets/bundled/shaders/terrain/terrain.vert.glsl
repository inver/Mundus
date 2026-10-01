/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_transMatrix;
uniform mat4 u_projViewMatrix;
uniform vec3 u_camPos;

// Fog
uniform float u_fogDensity;
uniform float u_fogGradient;

uniform vec2 u_terrainSize;

varying vec2 v_texCoord0;
varying vec2 splatPosition;
varying float v_fog;
varying vec3 v_normal;

#ifdef PICKER
varying vec3 v_pos;
#endif

void main(void) {
    // position
    vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);
    gl_Position = u_projViewMatrix * worldPos;

    // normal for lighting: transformed by the cofactor matrix (inverse transpose) so non uniform scale is correct
    // (GLSL 110 has no mat3(mat4), so the columns are used directly)
    vec3 c0 = u_transMatrix[0].xyz;
    vec3 c1 = u_transMatrix[1].xyz;
    vec3 c2 = u_transMatrix[2].xyz;
    vec3 cofactor0 = cross(c1, c2);
    float handedness = dot(c0, cofactor0) < 0.0 ? -1.0 : 1.0;
    mat3 normalMatrix = mat3(cofactor0, cross(c2, c0), cross(c0, c1));
    v_normal = normalize(normalMatrix * a_normal) * handedness;

    // texture stuff
    v_texCoord0 = a_texCoord0;
    splatPosition = vec2(a_position.x / u_terrainSize.x, a_position.z / u_terrainSize.y);

    // fog
    if(u_fogDensity > 0.0 && u_fogGradient > 0.0) {
        v_fog = distance(worldPos, vec4(u_camPos, 1.0));
        v_fog = exp(-pow(v_fog * u_fogDensity, u_fogGradient));
        v_fog = 1.0 - clamp(v_fog, 0.0, 1.0);
    } else {
        v_fog = 0.0;
    }

    #ifdef PICKER
    v_pos = worldPos.xyz;
    #endif

}
