package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.graphics.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BaseLight {
    protected Color color = new Color(1, 1, 1, 1);
    protected float intensity = 1f;
}
