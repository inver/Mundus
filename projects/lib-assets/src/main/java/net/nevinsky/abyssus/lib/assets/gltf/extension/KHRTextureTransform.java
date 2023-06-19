package net.nevinsky.abyssus.lib.assets.gltf.extension;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KHRTextureTransform implements Extension {
    public static final String NAME = "KHR_texture_transform";

    private float[] offset = {0f, 0f};
    private float rotation = 0f;
    private float[] scale = {1f, 1f};
    private Integer texCoord;
}