package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.utils.Array;
import net.nevinsky.abyssus.core.model.PbrModelMaterial;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_AMBIENT;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_BUMP;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_DIFFUSE;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_EMISSIVE;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_NORMAL;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_REFLECTION;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_SHININESS;
import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_BASE_COLOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_EMISSIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_REFLECTIVE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_GLTF_ALPHACUTOFF;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_GLTF_ALPHAMODE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_METALLIC_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_NAME;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_OPACITY;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_ROUGHNESS_FACTOR;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_SHININESS;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_TWOSIDED;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiGetMaterialFloatArray;
import static org.lwjgl.assimp.Assimp.aiGetMaterialIntegerArray;
import static org.lwjgl.assimp.Assimp.aiGetMaterialString;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_AMBIENT;
import static org.lwjgl.assimp.Assimp.aiTextureType_BASE_COLOR;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_EMISSIVE;
import static org.lwjgl.assimp.Assimp.aiTextureType_HEIGHT;
import static org.lwjgl.assimp.Assimp.aiTextureType_LIGHTMAP;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NORMALS;
import static org.lwjgl.assimp.Assimp.aiTextureType_REFLECTION;
import static org.lwjgl.assimp.Assimp.aiTextureType_SHININESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_SPECULAR;
import static org.lwjgl.assimp.Assimp.aiTextureType_UNKNOWN;

/**
 * Converts an {@link AIMaterial} into a {@link ModelMaterial}. All native temporaries live on the {@link MemoryStack}.
 */
final class MaterialProcessor {

    private static final int[][] TEXTURE_USAGES = {
            {aiTextureType_DIFFUSE, USAGE_DIFFUSE},
            {aiTextureType_BASE_COLOR, USAGE_DIFFUSE},
            {aiTextureType_SPECULAR, USAGE_SPECULAR},
            {aiTextureType_AMBIENT, USAGE_AMBIENT},
            {aiTextureType_EMISSIVE, USAGE_EMISSIVE},
            {aiTextureType_NORMALS, USAGE_NORMAL},
            {aiTextureType_HEIGHT, USAGE_BUMP},
            {aiTextureType_SHININESS, USAGE_SHININESS},
            {aiTextureType_REFLECTION, USAGE_REFLECTION},
    };

    /**
     * Only meaningful for PBR materials: glTF maps the metallic-roughness texture to UNKNOWN and the occlusion texture
     * to LIGHTMAP.
     */
    private static final int[][] PBR_TEXTURE_USAGES = {
            {aiTextureType_UNKNOWN, PbrModelMaterial.USAGE_METALLIC_ROUGHNESS},
            {aiTextureType_LIGHTMAP, PbrModelMaterial.USAGE_OCCLUSION},
    };

    private final TextureProcessor textures;
    private final Set<String> usedIds = new HashSet<>();

    MaterialProcessor(TextureProcessor textures) {
        this.textures = textures;
    }

    ModelMaterial process(AIMaterial aiMaterial, int index) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var metallic = readFloat(aiMaterial, AI_MATKEY_METALLIC_FACTOR, stack);
            var roughness = readFloat(aiMaterial, AI_MATKEY_ROUGHNESS_FACTOR, stack);
            var baseColor = readColor(aiMaterial, AI_MATKEY_BASE_COLOR, stack);
            var pbr = isPbr(baseColor, metallic, roughness);

            var material = pbr ? readPbr(aiMaterial, baseColor, metallic, roughness, stack) : new ModelMaterial();
            material.textures = new Array<>();
            material.id = uniqueId(readName(aiMaterial, stack), index);
            material.diffuse = readColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, stack);
            material.ambient = readColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, stack);
            material.specular = readColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, stack);
            material.emissive = readColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE, stack);
            material.reflection = readColor(aiMaterial, AI_MATKEY_COLOR_REFLECTIVE, stack);

            var opacity = readFloat(aiMaterial, AI_MATKEY_OPACITY, stack);
            if (opacity != null) {
                material.opacity = opacity;
            }
            var shininess = readFloat(aiMaterial, AI_MATKEY_SHININESS, stack);
            if (shininess != null) {
                material.shininess = shininess;
            }
            readTextures(aiMaterial, material, stack, TEXTURE_USAGES);
            if (pbr) {
                readTextures(aiMaterial, material, stack, PBR_TEXTURE_USAGES);
            }
            return material;
        }
    }

    /**
     * Some importers (e.g. OBJ) report the default metallic 0 and roughness 1 for every material, so the factors
     * alone don't make a material PBR. A base color is only set by PBR aware importers like glTF.
     */
    private static boolean isPbr(Color baseColor, Float metallic, Float roughness) {
        return baseColor != null || metallic != null && metallic != 0f || roughness != null && roughness != 1f;
    }

    private static PbrModelMaterial readPbr(AIMaterial aiMaterial, Color baseColor, Float metallic, Float roughness,
                                            MemoryStack stack) {
        var material = new PbrModelMaterial();
        material.metallic = metallic;
        material.roughness = roughness;
        material.baseColor = baseColor;
        var twoSided = readInt(aiMaterial, AI_MATKEY_TWOSIDED, stack);
        material.doubleSided = twoSided != null && twoSided != 0;

        var alphaMode = AIString.calloc(stack);
        if (aiGetMaterialString(aiMaterial, AI_MATKEY_GLTF_ALPHAMODE, aiTextureType_NONE, 0, alphaMode)
                == aiReturn_SUCCESS) {
            switch (alphaMode.dataString()) {
                case "MASK":
                    material.alphaMode = PbrModelMaterial.AlphaMode.MASK;
                    break;
                case "BLEND":
                    material.alphaMode = PbrModelMaterial.AlphaMode.BLEND;
                    break;
                default:
                    material.alphaMode = PbrModelMaterial.AlphaMode.OPAQUE;
            }
        }
        var cutoff = readFloat(aiMaterial, AI_MATKEY_GLTF_ALPHACUTOFF, stack);
        if (cutoff != null) {
            material.alphaCutoff = cutoff;
        }
        return material;
    }

    private String uniqueId(String name, int index) {
        var base = name == null || name.isEmpty() ? "material_" + index : name;
        var id = base;
        for (int n = 1; !usedIds.add(id); n++) {
            id = base + "_" + n;
        }
        return id;
    }

    private static String readName(AIMaterial aiMaterial, MemoryStack stack) {
        var name = AIString.calloc(stack);
        if (aiGetMaterialString(aiMaterial, AI_MATKEY_NAME, aiTextureType_NONE, 0, name) != aiReturn_SUCCESS) {
            return null;
        }
        return name.dataString();
    }

    private static Color readColor(AIMaterial aiMaterial, String key, MemoryStack stack) {
        var color = AIColor4D.calloc(stack);
        if (aiGetMaterialColor(aiMaterial, key, aiTextureType_NONE, 0, color) != aiReturn_SUCCESS) {
            return null;
        }
        return new Color(color.r(), color.g(), color.b(), color.a());
    }

    private static Integer readInt(AIMaterial aiMaterial, String key, MemoryStack stack) {
        var value = stack.mallocInt(1);
        var max = stack.ints(1);
        if (aiGetMaterialIntegerArray(aiMaterial, key, aiTextureType_NONE, 0, value, max) != aiReturn_SUCCESS) {
            return null;
        }
        return value.get(0);
    }

    private static Float readFloat(AIMaterial aiMaterial, String key, MemoryStack stack) {
        var value = stack.mallocFloat(1);
        var max = stack.ints(1);
        if (aiGetMaterialFloatArray(aiMaterial, key, aiTextureType_NONE, 0, value, max) != aiReturn_SUCCESS) {
            return null;
        }
        return value.get(0);
    }

    private static boolean hasUsage(ModelMaterial material, int usage) {
        for (var texture : material.textures) {
            if (texture.usage == usage) {
                return true;
            }
        }
        return false;
    }

    private void readTextures(AIMaterial aiMaterial, ModelMaterial material, MemoryStack stack, int[][] usages) {
        for (var entry : usages) {
            var path = AIString.calloc(stack);
            var res = aiGetMaterialTexture(aiMaterial, entry[0], 0, path, (IntBuffer) null, null, null, null, null,
                    null);
            if (res != aiReturn_SUCCESS) {
                continue;
            }
            if (hasUsage(material, entry[1])) {
                // e.g. glTF reports a base color texture as both DIFFUSE and BASE_COLOR
                continue;
            }
            var fileName = textures.resolve(path.dataString());
            if (fileName == null) {
                continue;
            }
            var texture = new ModelTexture();
            texture.id = material.id + "_" + material.textures.size;
            texture.usage = entry[1];
            texture.fileName = fileName;
            material.textures.add(texture);
        }
    }
}
