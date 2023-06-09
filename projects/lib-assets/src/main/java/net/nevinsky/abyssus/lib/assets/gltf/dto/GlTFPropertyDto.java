package net.nevinsky.abyssus.lib.assets.gltf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * glTF Property
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFPropertyDto {
    /**
     * JSON object with extension-specific objects.
     */
    protected Object extensions;

    /**
     * Application-specific data.
     * <p>
     * Although `extras` **MAY** have any type, it is common for applications to store and access custom data as
     * key/value pairs. Therefore, `extras` **SHOULD** be a JSON object rather than a primitive value for best portability.
     */
    protected Object extras;
}
