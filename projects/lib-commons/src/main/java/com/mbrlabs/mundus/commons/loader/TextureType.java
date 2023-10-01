package com.mbrlabs.mundus.commons.loader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_AMBIENT;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_DIFFUSE;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_EMISSIVE;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_NONE;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_NORMAL;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_REFLECTION;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_SHININESS;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_SPECULAR;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_UNKNOWN;
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

@Getter
@RequiredArgsConstructor
public enum TextureType {

    NONE(aiTextureType_NONE, USAGE_NONE),
    DIFFUSE(aiTextureType_DIFFUSE, USAGE_DIFFUSE),
    SPECULAR(aiTextureType_SPECULAR, USAGE_SPECULAR),
    AMBIENT(aiTextureType_AMBIENT, USAGE_AMBIENT),
    EMISSIVE(aiTextureType_EMISSIVE, USAGE_EMISSIVE),
    HEIGHT(aiTextureType_HEIGHT, USAGE_UNKNOWN),
    NORMALS(aiTextureType_NORMALS, USAGE_NORMAL),
    SHININESS(aiTextureType_SHININESS, USAGE_SHININESS),
    OPACITY(aiTextureType_OPACITY, USAGE_UNKNOWN),
    DISPLACEMENT(aiTextureType_DISPLACEMENT, USAGE_UNKNOWN),
    LIGHTMAP(aiTextureType_LIGHTMAP, USAGE_UNKNOWN),
    REFLECTION(aiTextureType_REFLECTION, USAGE_REFLECTION),
    BASE_COLOR(aiTextureType_BASE_COLOR, USAGE_UNKNOWN),
    NORMAL_CAMERA(aiTextureType_NORMAL_CAMERA, USAGE_UNKNOWN),
    EMISSION_COLOR(aiTextureType_EMISSION_COLOR, USAGE_UNKNOWN),
    METALNESS(aiTextureType_METALNESS, USAGE_UNKNOWN),
    DIFFUSE_ROUGHNESS(aiTextureType_DIFFUSE_ROUGHNESS, USAGE_UNKNOWN),
    AMBIENT_OCCLUSION(aiTextureType_AMBIENT_OCCLUSION, USAGE_UNKNOWN),
    SHEEN(aiTextureType_SHEEN, USAGE_UNKNOWN),
    CLEARCOAT(aiTextureType_CLEARCOAT, USAGE_UNKNOWN),
    TRANSMISSION(aiTextureType_TRANSMISSION, USAGE_UNKNOWN);
//    UNKNOWN(aiTextureType_UNKNOWN);

    private final int value;
    private final int textureUsage;
}
