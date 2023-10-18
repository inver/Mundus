package net.nevinsky.abyssus.lib.assets.gltf.dto.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Material Normal Texture Info
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFNormalTextureInfoDto extends GlTFTextureInfoDto {
    /**
     * The scalar parameter applied to each normal vector of the texture. This value scales the normal vector in X and Y
     * directions using the formula: `scaledNormal =  normalize((<sampled normal texture value> * 2.0 - 1.0)
     * * vec3(<normal scale>, <normal scale>, 1.0))`.
     */
    private Float scale = null;
}
