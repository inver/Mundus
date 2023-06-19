package net.nevinsky.abyssus.lib.assets.gltf.dto.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFPropertyDto;

/**
 * Reference to a texture.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFTextureInfoDto extends GlTFPropertyDto {
    /**
     * The index of the texture.
     */
    protected int index;
    /**
     * This integer value is used to construct a string in the format `TEXCOORD_<set index>` which is a reference to a
     * key in `mesh.primitives.attributes` (e.g. a value of `0` corresponds to `TEXCOORD_0`). A mesh primitive **MUST**
     * have the corresponding texture coordinate attributes for the material to be applicable to it.
     */
    protected Integer texCoord;
}
