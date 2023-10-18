package net.nevinsky.abyssus.lib.assets.gltf.dto.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A set of parameter values that are used to define the metallic-roughness material model from Physically-Based
 * Rendering (PBR) methodology.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFPBRMetallicRoughnessDto {
    /**
     * The factors for the base color of the material. This value defines linear multipliers for the sampled texels of
     * the base color texture.
     */
    private float[] baseColorFactor = null;

    /**
     * The base color texture. The first three components (RGB) **MUST** be encoded with the sRGB transfer function.
     * They specify the base color of the material. If the fourth component (A) is present, it represents the linear
     * alpha coverage of the material. Otherwise, the alpha coverage is equal to `1.0`. The `material.alphaMode`
     * property specifies how alpha is interpreted. The stored texels **MUST NOT** be premultiplied. When undefined,
     * the texture **MUST** be sampled as having `1.0` in all components.
     */
    private GlTFTextureInfoDto baseColorTexture;
    /**
     * The factor for the metalness of the material. This value defines a linear multiplier for the sampled metalness
     * values of the metallic-roughness texture.
     */
    private Float metallicFactor;
    /**
     * The factor for the roughness of the material. This value defines a linear multiplier for the sampled roughness
     * values of the metallic-roughness texture.
     */
    private Float roughnessFactor;
    /**
     * The metallic-roughness texture. The metalness values are sampled from the B channel. The roughness values are
     * sampled from the G channel. These values **MUST** be encoded with a linear transfer function. If other channels
     * are present (R or A), they **MUST** be ignored for metallic-roughness calculations. When undefined, the texture
     * **MUST** be sampled as having `1.0` in G and B components.
     */
    private GlTFTextureInfoDto metallicRoughnessTexture;
}
