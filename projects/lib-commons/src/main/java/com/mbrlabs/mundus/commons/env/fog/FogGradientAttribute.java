package com.mbrlabs.mundus.commons.env.fog;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class FogGradientAttribute extends FloatAttribute {
    public static final String FOG_GRADIENT_ALIAS = "fogGradient";
    public static final long FOG_GRADIENT = register(FOG_GRADIENT_ALIAS);

    public FogGradientAttribute(float value) {
        super(FOG_GRADIENT, value);
    }
}
