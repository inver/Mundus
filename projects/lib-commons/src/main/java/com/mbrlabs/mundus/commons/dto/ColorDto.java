package com.mbrlabs.mundus.commons.dto;

import com.badlogic.gdx.graphics.Color;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ColorDto {
    private float r, g, b, a;

    public ColorDto(float[] floatArray) {
        if (floatArray == null || floatArray.length != 4) {
            throw new IllegalArgumentException();
        }

        r = floatArray[0];
        g = floatArray[1];
        b = floatArray[2];
        a = floatArray[2];
    }

    @JsonCreator
    public ColorDto(@JsonProperty("r") float r, @JsonProperty("g") float g, @JsonProperty("b") float b,
                    @JsonProperty("a") float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public ColorDto(Color color) {
        this(color.r, color.g, color.b, color.a);
    }

    public Color toColor() {
        return new Color(r, g, b, a);
    }
}
