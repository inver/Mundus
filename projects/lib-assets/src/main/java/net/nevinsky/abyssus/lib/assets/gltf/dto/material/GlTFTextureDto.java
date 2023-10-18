package net.nevinsky.abyssus.lib.assets.gltf.dto.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFChildOfRootPropertyDto;

/**
 * A texture and its sampler.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFTextureDto extends GlTFChildOfRootPropertyDto {
    /**
     * The index of the sampler used by this texture. When undefined, a sampler with repeat wrapping and auto filtering
     * **SHOULD** be used.
     */
    private Integer sampler;
    /**
     * The index of the image used by this texture. When undefined, an extension or other mechanism **SHOULD** supply
     * an alternate texture source, otherwise behavior is undefined.
     */
    private Integer source;
}
