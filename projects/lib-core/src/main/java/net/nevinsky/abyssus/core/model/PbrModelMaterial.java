package net.nevinsky.abyssus.core.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;

/**
 * A {@link ModelMaterial} with metallic-roughness (glTF) properties. {@code null} values are not set on the material.
 * Textures use the {@code USAGE_*} constants of this class in addition to the ones of
 * {@link com.badlogic.gdx.graphics.g3d.model.data.ModelTexture}.
 */
public class PbrModelMaterial extends ModelMaterial {

    public static final int USAGE_METALLIC_ROUGHNESS = 100;
    public static final int USAGE_OCCLUSION = 101;

    public enum AlphaMode {
        OPAQUE, MASK, BLEND
    }

    public Color baseColor;
    public Float metallic;
    public Float roughness;
    public boolean doubleSided;
    public AlphaMode alphaMode = AlphaMode.OPAQUE;
    public float alphaCutoff = 0.5f;
}
