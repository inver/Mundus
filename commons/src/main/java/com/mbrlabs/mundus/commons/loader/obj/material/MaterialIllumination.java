package com.mbrlabs.mundus.commons.loader.obj.material;

public enum MaterialIllumination {
    // Color on and Ambient off
    COLOR_ON_AND_AMBIENT_OFF(0),
    COLOR_ON_AND_AMBIENT_ON(1),
    HIGHLIGHT_ON(2),
    REFLECTION_AND_RAYTRACE_ON(3),
    TRANSPARENCY_GLASS_AND_REFLECTION_RAYTRACE_ON(4),
    REFLECTION_FRESNEL_ON_AND_RAYTRACE_ON(5),
    TRANSPARENCY_REFRACTION_ON_AND_REFLECTION_FRESNEL_OFF_AND_RAYTRACE_ON(6),
    TRANSPARENCY_REFRACTION_ON_AND_REFLECTION_FRESNEL_ON_AND_RAYTRACE_ON(7),
    REFLECTION_ON_AND_RAYTRACE_OFF(8),
    TRANSPARENCY_GLASS_ON(9),
    CAST_SHADOWS_ONTO_INVISIBLE_SURFACES(10);

    private final int value;

    MaterialIllumination(int value) {
        this.value = value;
    }

    public static MaterialIllumination of(int value) {
        for (var v : values()) {
            if (v.value == value) {
                return v;
            }
        }

        throw new IllegalArgumentException("Unknown value " + value);
    }
}
