package com.mbrlabs.mundus.commons.env.fog;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class FogDensityAttribute extends FloatAttribute {

    public static final String FOG_DENSITY_ALIAS = "fogDensity";
    public static final long FOG_DENSITY = register(FOG_DENSITY_ALIAS);

    public FogDensityAttribute(float value) {
        super(FOG_DENSITY, value);
    }
}
