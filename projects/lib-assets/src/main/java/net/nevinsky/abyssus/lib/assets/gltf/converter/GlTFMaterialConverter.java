package net.nevinsky.abyssus.lib.assets.gltf.converter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRColorAttribute;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRFlagAttribute;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRFloatAttribute;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRTextureAttribute;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRVolumeAttribute;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.material.GlTFMaterialDto;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRMaterialsIOR;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRMaterialsTransmission;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRMaterialsUnlit;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRMaterialsVolume;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GlTFMaterialConverter {

    private final GlTFTextureConverter textureConverter;

    public Material convert(GlTFDto root, GlTFMaterialDto dto) {
        Material material = new Material();
        if (dto.getName() != null) {
            material.id = dto.getName();
        }

        if (dto.getEmissiveFactor() != null) {
            material.set(new ColorAttribute(ColorAttribute.Emissive, mapColor(dto.getEmissiveFactor(), Color.BLACK)));
        }

        if (dto.getEmissiveTexture() != null) {
            material.set(textureConverter.getTextureMap(root, PBRTextureAttribute.EmissiveTexture,
                    dto.getEmissiveTexture()));
        }

        if (BooleanUtils.isTrue(dto.getDoubleSided())) {
            material.set(IntAttribute.createCullFace(0)); // 0 to disable culling
        }

        if (dto.getNormalTexture() != null) {
            material.set(textureConverter.getTextureMap(root, PBRTextureAttribute.NormalTexture,
                    dto.getNormalTexture()));
            material.set(PBRFloatAttribute.createNormalScale(dto.getNormalTexture().getScale()));
        }

        if (dto.getOcclusionTexture() != null) {
            material.set(textureConverter.getTextureMap(root, PBRTextureAttribute.OcclusionTexture,
                    dto.getOcclusionTexture()));
            material.set(PBRFloatAttribute.createOcclusionStrength(dto.getOcclusionTexture().getStrength()));
        }

        boolean alphaBlend = false;
        if (dto.getAlphaMode() == GlTFMaterialDto.AlphaMode.MASK) {
            float value = dto.getAlphaCutoff() == null ? 0.5f : dto.getAlphaCutoff();
            material.set(FloatAttribute.createAlphaTest(value));
            material.set(new BlendingAttribute()); // necessary
        } else if (dto.getAlphaMode() == GlTFMaterialDto.AlphaMode.BLEND) {
            material.set(new BlendingAttribute()); // opacity is set by pbrMetallicRoughness below
            alphaBlend = true;
        }

        if (dto.getPbrMetallicRoughness() != null) {
            var p = dto.getPbrMetallicRoughness();

            var baseColorFactor = mapColor(p.getBaseColorFactor(), Color.WHITE);
            material.set(new PBRColorAttribute(PBRColorAttribute.BaseColorFactor, baseColorFactor));

            material.set(PBRFloatAttribute.createMetallic(p.getMetallicFactor()));
            material.set(PBRFloatAttribute.createRoughness(p.getRoughnessFactor()));

            if (p.getMetallicRoughnessTexture() != null) {
                material.set(textureConverter.getTextureMap(root, PBRTextureAttribute.MetallicRoughnessTexture,
                        p.getMetallicRoughnessTexture()));
            }

            if (p.getBaseColorTexture() != null) {
                material.set(textureConverter.getTextureMap(root, PBRTextureAttribute.BaseColorTexture,
                        p.getBaseColorTexture()));
            }

            if (alphaBlend) {
                material.get(BlendingAttribute.class, BlendingAttribute.Type).opacity = baseColorFactor.a;
            }
        }

        // can have both PBR base and ext
        if (dto.getExtensions() != null) {
            processMaterialExtensions(root, dto, material);
        }

        return material;
    }

    private Color mapColor(float[] c, Color defaultColor) {
        if (c == null) {
            return new Color(defaultColor);
        }
        if (c.length < 4) {
            return new Color(c[0], c[1], c[2], 1f);
        } else {
            return new Color(c[0], c[1], c[2], c[3]);
        }
    }

    private void processMaterialExtensions(GlTFDto root, GlTFMaterialDto dto, Material material) {
        processKHRMaterialsUnlit(dto, material);
        processKHRMaterialsTransmission(root, dto, material);
        processKHRMaterialsVolume(root, dto, material);
        processKHRMaterialsIOR(dto, material);
//        {
//            KHRMaterialsSpecular ext = dto.getExtension(KHRMaterialsSpecular.class, KHRMaterialsSpecular.EXT);
//            if (ext != null) {
//                material.set(PBRFloatAttribute.createSpecularFactor(ext.specularFactor));
//                material.set(new PBRHDRColorAttribute(PBRHDRColorAttribute.Specular, ext.specularColorFactor[0],
//                ext.specularColorFactor[1], ext.specularColorFactor[2]));
//                if (ext.specularTexture != null) {
//                    material.set(getTexureMap(PBRTextureAttribute.SpecularFactorTexture, ext.specularTexture));
//                }
//                if (ext.specularColorTexture != null) {
//                    material.set(getTexureMap(PBRTextureAttribute.SpecularColorTexture, ext.specularColorTexture));
//                }
//            }
//        }
//        {
//            KHRMaterialsIridescence ext = dto.getExtension(KHRMaterialsIridescence.class,
//            KHRMaterialsIridescence.EXT);
//            if (ext != null) {
//                material.set(new PBRIridescenceAttribute(ext.iridescenceFactor, ext.iridescenceIor,
//                ext.iridescenceThicknessMinimum, ext.iridescenceThicknessMaximum));
//                if (ext.iridescenceTexture != null) {
//                    material.set(getTexureMap(PBRTextureAttribute.IridescenceTexture, ext.iridescenceTexture));
//                }
//                if (ext.iridescenceThicknessTexture != null) {
//                    material.set(getTexureMap(PBRTextureAttribute.IridescenceThicknessTexture,
//                    ext.iridescenceThicknessTexture));
//                }
//            }
//        }
//        {
//            KHRMaterialsEmissiveStrength ext = dto.getExtension(KHRMaterialsEmissiveStrength.class,
//            KHRMaterialsEmissiveStrength.EXT);
//            if (ext != null) {
//                material.set(PBRFloatAttribute.createEmissiveIntensity(ext.emissiveStrength));
//            }
//        }
    }

    private static void processKHRMaterialsIOR(GlTFMaterialDto dto, Material material) {
        var ext = dto.getExtension(KHRMaterialsIOR.class);
        if (ext != null) {
            material.set(PBRFloatAttribute.createIOR(ext.getIor()));
        }
    }

    private void processKHRMaterialsVolume(GlTFDto root, GlTFMaterialDto dto, Material material) {
        KHRMaterialsVolume ext = dto.getExtension(KHRMaterialsVolume.class);
        if (ext != null) {
            material.set(new PBRVolumeAttribute(ext.getThicknessFactor(),
                    ext.getAttenuationDistance() == null ? 0f : ext.getAttenuationDistance(),
                    mapColor(ext.getAttenuationColor(), Color.WHITE)));
            if (ext.getThicknessTexture() != null) {
                material.set(textureConverter.getTextureMap(root, PBRTextureAttribute.ThicknessTexture,
                        ext.getThicknessTexture()));
            }
        }
    }

    private void processKHRMaterialsTransmission(GlTFDto root, GlTFMaterialDto dto, Material material) {
        var kHRMaterialsTransmission = dto.getExtension(KHRMaterialsTransmission.class);
        if (kHRMaterialsTransmission != null) {
            material.set(PBRFloatAttribute.createTransmissionFactor(kHRMaterialsTransmission.getTransmissionFactor()));
            if (kHRMaterialsTransmission.getTransmissionTexture() != null) {
                material.set(textureConverter.getTextureMap(root, PBRTextureAttribute.TransmissionTexture,
                        kHRMaterialsTransmission.getTransmissionTexture()));
            }
        }
    }

    private void processKHRMaterialsUnlit(GlTFMaterialDto dto, Material material) {
        if (dto.getExtension(KHRMaterialsUnlit.class) != null) {
            material.set(new PBRFlagAttribute(PBRFlagAttribute.Unlit));
        }
    }

    public List<Material> converts(GlTFDto root) {
        return root.getMaterials().stream()
                .map(m -> convert(root, m))
                .collect(Collectors.toList());
    }
}
