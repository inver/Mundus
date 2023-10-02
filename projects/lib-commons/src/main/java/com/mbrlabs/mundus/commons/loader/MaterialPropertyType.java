package com.mbrlabs.mundus.commons.loader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_ANISOTROPY_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_BASE_COLOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_BLEND_FUNC;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_BUMPSCALING;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_CLEARCOAT_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_CLEARCOAT_ROUGHNESS_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_EMISSIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_REFLECTIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_TRANSPARENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_EMISSIVE_INTENSITY;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_ENABLE_WIREFRAME;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_GLOBAL_BACKGROUND_IMAGE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_GLOBAL_SHADERLANG;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_GLOSSINESS_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_GLTF_ALPHACUTOFF;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_GLTF_ALPHAMODE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_METALLIC_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_NAME;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_OPACITY;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_REFLECTIVITY;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_REFRACTI;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_ROUGHNESS_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHADER_COMPUTE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHADER_FRAGMENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHADER_GEO;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHADER_PRIMITIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHADER_TESSELATION;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHADER_VERTEX;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHADING_MODEL;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHEEN_COLOR_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHEEN_ROUGHNESS_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHININESS;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHININESS_STRENGTH;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SPECULAR_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_TRANSMISSION_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_TRANSPARENCYFACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_TWOSIDED;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_USE_AO_MAP;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_USE_COLOR_MAP;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_USE_EMISSIVE_MAP;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_USE_METALLIC_MAP;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_USE_ROUGHNESS_MAP;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_VOLUME_ATTENUATION_COLOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_VOLUME_ATTENUATION_DISTANCE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_VOLUME_THICKNESS_FACTOR;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_GLTF_MAPPINGFILTER_MAG_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_GLTF_MAPPINGFILTER_MIN_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_GLTF_MAPPINGID_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_GLTF_MAPPINGNAME_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_GLTF_SCALE_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_GLTF_STRENGTH_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_MAPPINGMODE_U_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_MAPPINGMODE_V_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_MAPPING_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_OBJ_BUMPMULT_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_TEXBLEND_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_TEXFLAGS_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_TEXMAP_AXIS_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_TEXOP_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_TEXTURE_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_UVTRANSFORM_BASE;
import static org.lwjgl.assimp.Assimp._AI_MATKEY_UVWSRC_BASE;

@Getter
@RequiredArgsConstructor
public enum MaterialPropertyType {
    NAME(AI_MATKEY_NAME),
    TWOSIDED(AI_MATKEY_TWOSIDED),
    SHADING_MODEL(AI_MATKEY_SHADING_MODEL),
    ENABLE_WIREFRAME(AI_MATKEY_ENABLE_WIREFRAME),
    BLEND_FUNC(AI_MATKEY_BLEND_FUNC),
    OPACITY(AI_MATKEY_OPACITY),
    TRANSPARENCYFACTOR(AI_MATKEY_TRANSPARENCYFACTOR),
    BUMPSCALING(AI_MATKEY_BUMPSCALING),
    SHININESS(AI_MATKEY_SHININESS),
    REFLECTIVITY(AI_MATKEY_REFLECTIVITY),
    SHININESS_STRENGTH(AI_MATKEY_SHININESS_STRENGTH),
    REFRACTI(AI_MATKEY_REFRACTI),
    COLOR_DIFFUSE(AI_MATKEY_COLOR_DIFFUSE),
    COLOR_AMBIENT(AI_MATKEY_COLOR_AMBIENT),
    COLOR_SPECULAR(AI_MATKEY_COLOR_SPECULAR),
    COLOR_EMISSIVE(AI_MATKEY_COLOR_EMISSIVE),
    COLOR_TRANSPARENT(AI_MATKEY_COLOR_TRANSPARENT),
    COLOR_REFLECTIVE(AI_MATKEY_COLOR_REFLECTIVE),
    GLOBAL_BACKGROUND_IMAGE(AI_MATKEY_GLOBAL_BACKGROUND_IMAGE),
    GLOBAL_SHADERLANG(AI_MATKEY_GLOBAL_SHADERLANG),
    SHADER_VERTEX(AI_MATKEY_SHADER_VERTEX),
    SHADER_FRAGMENT(AI_MATKEY_SHADER_FRAGMENT),
    SHADER_GEO(AI_MATKEY_SHADER_GEO),
    SHADER_TESSELATION(AI_MATKEY_SHADER_TESSELATION),
    SHADER_PRIMITIVE(AI_MATKEY_SHADER_PRIMITIVE),
    SHADER_COMPUTE(AI_MATKEY_SHADER_COMPUTE),
    USE_COLOR_MAP(AI_MATKEY_USE_COLOR_MAP),
    BASE_COLOR(AI_MATKEY_BASE_COLOR),
    USE_METALLIC_MAP(AI_MATKEY_USE_METALLIC_MAP),
    METALLIC_FACTOR(AI_MATKEY_METALLIC_FACTOR),
    USE_ROUGHNESS_MAP(AI_MATKEY_USE_ROUGHNESS_MAP),
    ROUGHNESS_FACTOR(AI_MATKEY_ROUGHNESS_FACTOR),
    ANISOTROPY_FACTOR(AI_MATKEY_ANISOTROPY_FACTOR),
    SPECULAR_FACTOR(AI_MATKEY_SPECULAR_FACTOR),
    GLOSSINESS_FACTOR(AI_MATKEY_GLOSSINESS_FACTOR),
    SHEEN_COLOR_FACTOR(AI_MATKEY_SHEEN_COLOR_FACTOR),
    SHEEN_ROUGHNESS_FACTOR(AI_MATKEY_SHEEN_ROUGHNESS_FACTOR),
    CLEARCOAT_FACTOR(AI_MATKEY_CLEARCOAT_FACTOR),
    CLEARCOAT_ROUGHNESS_FACTOR(AI_MATKEY_CLEARCOAT_ROUGHNESS_FACTOR),
    TRANSMISSION_FACTOR(AI_MATKEY_TRANSMISSION_FACTOR),
    VOLUME_THICKNESS_FACTOR(AI_MATKEY_VOLUME_THICKNESS_FACTOR),
    VOLUME_ATTENUATION_DISTANCE(AI_MATKEY_VOLUME_ATTENUATION_DISTANCE),
    VOLUME_ATTENUATION_COLOR(AI_MATKEY_VOLUME_ATTENUATION_COLOR),
    USE_EMISSIVE_MAP(AI_MATKEY_USE_EMISSIVE_MAP),
    EMISSIVE_INTENSITY(AI_MATKEY_EMISSIVE_INTENSITY),
    USE_AO_MAP(AI_MATKEY_USE_AO_MAP),
    TEXTURE_BASE(_AI_MATKEY_TEXTURE_BASE),
    UVWSRC_BASE(_AI_MATKEY_UVWSRC_BASE),
    TEXOP_BASE(_AI_MATKEY_TEXOP_BASE),
    MAPPING_BASE(_AI_MATKEY_MAPPING_BASE),
    TEXBLEND_BASE(_AI_MATKEY_TEXBLEND_BASE),
    MAPPINGMODE_U_BASE(_AI_MATKEY_MAPPINGMODE_U_BASE),
    MAPPINGMODE_V_BASE(_AI_MATKEY_MAPPINGMODE_V_BASE),
    TEXMAP_AXIS_BASE(_AI_MATKEY_TEXMAP_AXIS_BASE),
    UVTRANSFORM_BASE(_AI_MATKEY_UVTRANSFORM_BASE),
    TEXFLAGS_BASE(_AI_MATKEY_TEXFLAGS_BASE),
    OBJ_BUMPMULT_BASE(_AI_MATKEY_OBJ_BUMPMULT_BASE),
    GLTF_ALPHAMODE(AI_MATKEY_GLTF_ALPHAMODE),
    GLTF_ALPHACUTOFF(AI_MATKEY_GLTF_ALPHACUTOFF),
    GLTF_MAPPINGNAME_BASE(_AI_MATKEY_GLTF_MAPPINGNAME_BASE),
    GLTF_MAPPINGID_BASE(_AI_MATKEY_GLTF_MAPPINGID_BASE),
    GLTF_MAPPINGFILTER_MAG_BASE(_AI_MATKEY_GLTF_MAPPINGFILTER_MAG_BASE),
    GLTF_MAPPINGFILTER_MIN_BASE(_AI_MATKEY_GLTF_MAPPINGFILTER_MIN_BASE),
    GLTF_SCALE_BASE(_AI_MATKEY_GLTF_SCALE_BASE),
    GLTF_STRENGTH_BASE(_AI_MATKEY_GLTF_STRENGTH_BASE);

    private final String value;

    public static MaterialPropertyType of(String key) {
        for (var v : values()) {
            if (key.equalsIgnoreCase(v.value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Wrong value: " + key);
    }
}
