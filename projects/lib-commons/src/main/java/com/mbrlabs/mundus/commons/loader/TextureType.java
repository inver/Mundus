package com.mbrlabs.mundus.commons.loader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.lwjgl.assimp.Assimp.aiTextureType_AMBIENT;
import static org.lwjgl.assimp.Assimp.aiTextureType_AMBIENT_OCCLUSION;
import static org.lwjgl.assimp.Assimp.aiTextureType_BASE_COLOR;
import static org.lwjgl.assimp.Assimp.aiTextureType_CLEARCOAT;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE_ROUGHNESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_DISPLACEMENT;
import static org.lwjgl.assimp.Assimp.aiTextureType_EMISSION_COLOR;
import static org.lwjgl.assimp.Assimp.aiTextureType_EMISSIVE;
import static org.lwjgl.assimp.Assimp.aiTextureType_HEIGHT;
import static org.lwjgl.assimp.Assimp.aiTextureType_LIGHTMAP;
import static org.lwjgl.assimp.Assimp.aiTextureType_METALNESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NORMALS;
import static org.lwjgl.assimp.Assimp.aiTextureType_NORMAL_CAMERA;
import static org.lwjgl.assimp.Assimp.aiTextureType_OPACITY;
import static org.lwjgl.assimp.Assimp.aiTextureType_REFLECTION;
import static org.lwjgl.assimp.Assimp.aiTextureType_SHEEN;
import static org.lwjgl.assimp.Assimp.aiTextureType_SHININESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_SPECULAR;
import static org.lwjgl.assimp.Assimp.aiTextureType_TRANSMISSION;

@RequiredArgsConstructor
public enum TextureType {

    NONE(aiTextureType_NONE),
    DIFFUSE(aiTextureType_DIFFUSE),
    SPECULAR(aiTextureType_SPECULAR),
    AMBIENT(aiTextureType_AMBIENT),
    EMISSIVE(aiTextureType_EMISSIVE),
    HEIGHT(aiTextureType_HEIGHT),
    NORMALS(aiTextureType_NORMALS),
    SHININESS(aiTextureType_SHININESS),
    OPACITY(aiTextureType_OPACITY),
    DISPLACEMENT(aiTextureType_DISPLACEMENT),
    LIGHTMAP(aiTextureType_LIGHTMAP),
    REFLECTION(aiTextureType_REFLECTION),
    BASE_COLOR(aiTextureType_BASE_COLOR),
    NORMAL_CAMERA(aiTextureType_NORMAL_CAMERA),
    EMISSION_COLOR(aiTextureType_EMISSION_COLOR),
    METALNESS(aiTextureType_METALNESS),
    DIFFUSE_ROUGHNESS(aiTextureType_DIFFUSE_ROUGHNESS),
    AMBIENT_OCCLUSION(aiTextureType_AMBIENT_OCCLUSION),
    SHEEN(aiTextureType_SHEEN),
    CLEARCOAT(aiTextureType_CLEARCOAT),
    TRANSMISSION(aiTextureType_TRANSMISSION);
//    UNKNOWN(aiTextureType_UNKNOWN);

    @Getter
    private final int value;
}
