package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class LightIntensityAttribute extends FloatAttribute {
    public static final String AMBIENT_LIGHT_INTENSITY_ALIAS = "ambientLightIntensity";
    public static final long AMBIENT_LIGHT_INTENSITY = register(AMBIENT_LIGHT_INTENSITY_ALIAS);

    public LightIntensityAttribute(float value) {
        super(AMBIENT_LIGHT_INTENSITY, value);
    }
}
