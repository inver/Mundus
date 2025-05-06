package net.nevinsky.abyssus.lib.assets.gltf.extension;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.material.GlTFTextureInfoDto;

@NoArgsConstructor
@Getter
@Setter
public class KHRMaterialsVolume implements Extension {
    public static final String NAME = "KHR_materials_volume";

    private float thicknessFactor = 0f;
    private GlTFTextureInfoDto thicknessTexture = null;
    private Float attenuationDistance = null; // default +inf.
    private float[] attenuationColor = {1, 1, 1};
}