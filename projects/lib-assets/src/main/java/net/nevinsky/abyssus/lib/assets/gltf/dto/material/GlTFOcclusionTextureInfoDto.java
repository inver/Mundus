package net.nevinsky.abyssus.lib.assets.gltf.dto.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Material Occlusion Texture Info
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFOcclusionTextureInfoDto extends GlTFTextureInfoDto {
    /**
     * A scalar parameter controlling the amount of occlusion applied. A value of `0.0` means no occlusion. A value of
     * `1.0` means full occlusion. This value affects the final occlusion value as: `1.0 + strength *
     * (<sampled occlusion texture value> - 1.0)`.
     */
    private Float strength;
}
