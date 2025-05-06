package net.nevinsky.abyssus.lib.assets.gltf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A buffer points to binary geometry, animation, or skins.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFBufferDto extends GlTFChildOfRootPropertyDto {
    /**
     * The URI (or IRI) of the buffer.  Relative paths are relative to the current glTF asset.  Instead of referencing
     * an external file, this field **MAY** contain a `data:`-URI.
     */
    private String uri;
    /**
     * The length of the buffer in bytes.
     */
    private int byteLength;
}
