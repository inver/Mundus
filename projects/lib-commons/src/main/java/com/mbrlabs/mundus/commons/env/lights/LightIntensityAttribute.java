package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class LightIntensityAttribute extends FloatAttribute {
    public static final String AMBIENT_LIGHT_INTENSITY_ALIAS = "ambientLightIntensity";
    public static final long AmbientLightIntensity = register(AMBIENT_LIGHT_INTENSITY_ALIAS);

    public LightIntensityAttribute() {
        super(AmbientLightIntensity);
    }

    public LightIntensityAttribute(float value) {
        super(AmbientLightIntensity, value);
    }
}
