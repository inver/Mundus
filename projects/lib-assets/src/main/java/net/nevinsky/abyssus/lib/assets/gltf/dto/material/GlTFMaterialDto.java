package net.nevinsky.abyssus.lib.assets.gltf.dto.material;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFChildOfRootPropertyDto;

import java.util.Arrays;

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
    /**
     * The emissive texture. It controls the color and intensity of the light being emitted by the material. This
     * texture contains RGB components encoded with the sRGB transfer function. If a fourth component (A) is present,
     * it **MUST** be ignored. When undefined, the texture **MUST** be sampled as having `1.0` in RGB components.
     */
    private GlTFTextureInfoDto emissiveTexture;
    /**
     * The factors for the emissive color of the material. This value defines linear multipliers for the sampled texels
     * of the emissive texture.
     */
    private float[] emissiveFactor;
    /**
     * The material's alpha rendering mode enumeration specifying the interpretation of the alpha value of the base
     * color.
     */
    private AlphaMode alphaMode;
    /**
     * Specifies the cutoff threshold when in `MASK` alpha mode. If the alpha value is greater than or equal to this
     * value then it is rendered as fully opaque, otherwise, it is rendered as fully transparent. A value greater than
     * `1.0` will render the entire material as fully transparent. This value **MUST** be ignored for other alpha modes.
     * When `alphaMode` is not defined, this value **MUST NOT** be defined.
     */
    private Float alphaCutoff;
    /**
     * Specifies whether the material is double sided. When this value is false, back-face culling is enabled. When this
     * value is true, back-face culling is disabled and double-sided lighting is enabled. The back-face **MUST** have
     * its normals reversed before the lighting equation is evaluated.
     */
    private Boolean doubleSided;

    @RequiredArgsConstructor
    public enum AlphaMode {
        /**
         * The alpha value is ignored, and the rendered output is fully opaque.
         */
        OPAQUE,
        /**
         * The rendered output is either fully opaque or fully transparent depending on the alpha value and the
         * specified `alphaCutoff` value; the exact appearance of the edges **MAY** be subject to
         * implementation-specific techniques such as \"`Alpha-to-Coverage`\".
         */
        MASK,
        /**
         * The alpha value is used to composite the source and destination areas. The rendered output is combined with
         * the background using the normal painting operation (i.e. the Porter and Duff over operator).
         */
        BLEND;

        @JsonValue
        public String getValue() {
            return name();
        }

        @JsonCreator
        public static AlphaMode from(String inputValue) {
            return Arrays.stream(values())
                    .filter(v -> v.name().equalsIgnoreCase(inputValue))
                    .findAny()
                    .orElse(null);
        }
    }
}
