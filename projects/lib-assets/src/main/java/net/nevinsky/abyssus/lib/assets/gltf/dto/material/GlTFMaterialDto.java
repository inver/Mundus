package net.nevinsky.abyssus.lib.assets.gltf.dto.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFChildOfRootPropertyDto;

/**
 * The material appearance of a primitive.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFMaterialDto extends GlTFChildOfRootPropertyDto {
    /**
     * A set of parameter values that are used to define the metallic-roughness material model from Physically Based
     * Rendering (PBR) methodology. When undefined, all the default values of `pbrMetallicRoughness` **MUST** apply.
     */
    private GlTFPBRMetallicRoughnessDto pbrMetallicRoughness;

    /**
     * The tangent space normal texture. The texture encodes RGB components with linear transfer function. Each texel
     * represents the XYZ components of a normal vector in tangent space. The normal vectors use the convention +X is
     * right and +Y is up. +Z points toward the viewer. If a fourth component (A) is present, it **MUST** be ignored.
     * When undefined, the material does not have a tangent space normal texture.
     */
    private GlTFNormalTextureInfoDto normalTexture;

    /**
     * The occlusion texture. The occlusion values are linearly sampled from the R channel. Higher values indicate areas
     * that receive full indirect lighting and lower values indicate no indirect lighting. If other channels are present
     * (GBA), they **MUST** be ignored for occlusion calculations. When undefined, the material does not have an
     * occlusion texture.
     */
    private GlTFOcclusionTextureInfoDto occlusionTexture;
}
