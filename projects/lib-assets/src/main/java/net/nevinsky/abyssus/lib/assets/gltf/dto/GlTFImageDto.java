package net.nevinsky.abyssus.lib.assets.gltf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Image data used to create a texture. Image **MAY** be referenced by an URI (or IRI) or a buffer view index.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFImageDto extends GlTFChildOfRootPropertyDto {
    /**
     * The URI (or IRI) of the image.  Relative paths are relative to the current glTF asset.  Instead of referencing
     * an external file, this field **MAY** contain a `data:`-URI. This field **MUST NOT** be defined when `bufferView`
     * is defined.
     */
    private String uri;
    /**
     * The image's media type. This field **MUST** be defined when `bufferView` is defined.
     */
    private String mimeType;
    /**
     * The index of the bufferView that contains the image. This field **MUST NOT** be defined when `uri` is defined.
     */
    private Integer bufferView = null;
}
