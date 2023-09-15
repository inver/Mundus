package com.mbrlabs.mundus.commons.loader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_EMISSIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_REFLECTIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_TRANSPARENT;

@Getter
@RequiredArgsConstructor
public enum ColorType {

    DIFFUSE(AI_MATKEY_COLOR_DIFFUSE),
    AMBIENT(AI_MATKEY_COLOR_AMBIENT),
    SPECULAR(AI_MATKEY_COLOR_SPECULAR),
    EMISSIVE(AI_MATKEY_COLOR_EMISSIVE),
    TRANSPARENT(AI_MATKEY_COLOR_TRANSPARENT),
    REFLECTIVE(AI_MATKEY_COLOR_REFLECTIVE);

    private final String value;
}
