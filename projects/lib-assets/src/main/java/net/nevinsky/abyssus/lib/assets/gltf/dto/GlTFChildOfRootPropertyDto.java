package net.nevinsky.abyssus.lib.assets.gltf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * glTF Child of Root Property
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFChildOfRootPropertyDto extends GlTFPropertyDto {
    /**
     * The user-defined name of this object.  This is not necessarily unique, e.g., an accessor and a buffer could have
     * the same name, or two accessors could even have the same name.
     */
    protected String name;
}
