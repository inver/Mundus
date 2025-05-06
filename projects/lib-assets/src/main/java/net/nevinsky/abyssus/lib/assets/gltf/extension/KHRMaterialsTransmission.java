package net.nevinsky.abyssus.lib.assets.gltf.extension;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.material.GlTFTextureInfoDto;

@Getter
@NoArgsConstructor
@Setter
public class KHRMaterialsTransmission implements Extension {

    public static final String NAME = "KHR_materials_transmission";

    private float transmissionFactor = 0;
    private GlTFTextureInfoDto transmissionTexture = null;
}