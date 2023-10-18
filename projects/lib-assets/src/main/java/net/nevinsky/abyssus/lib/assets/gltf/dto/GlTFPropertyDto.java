package net.nevinsky.abyssus.lib.assets.gltf.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.extension.Extension;
import net.nevinsky.abyssus.lib.assets.gltf.extension.Extensions;

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
    protected Extensions extensions;

    /**
     * Application-specific data.
     * <p>
     * Although `extras` **MAY** have any type, it is common for applications to store and access custom data as
     * key/value pairs. Therefore, `extras` **SHOULD** be a JSON object rather than a primitive value for best
     * portability.
     */
    protected Object extras;

    @JsonIgnore
    public <T extends Extension> T getExtension(Class<T> clazz) {
        if (extensions == null) {
            return null;
        }

        return extensions.get(clazz);
    }
}
