package net.nevinsky.abyssus.lib.assets.gltf;

import lombok.experimental.UtilityClass;
import net.nevinsky.abyssus.lib.assets.gltf.extension.Extension;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRMaterialsTransmission;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRMaterialsUnlit;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRMaterialsVolume;

import java.util.Map;

@UtilityClass
public class GlTFExtensionsConfiguration {

    public static final Map<String, Class<? extends Extension>> EXTENSIONS = Map.of(
            KHRMaterialsUnlit.NAME, KHRMaterialsUnlit.class,
            KHRMaterialsTransmission.NAME, KHRMaterialsTransmission.class,
            KHRMaterialsVolume.NAME, KHRMaterialsVolume.class
    );

    public Class<? extends Extension> getByName(String name) {
        return EXTENSIONS.get(name);
    }

    public boolean supports(String name) {
        return EXTENSIONS.containsKey(name);
    }
}
