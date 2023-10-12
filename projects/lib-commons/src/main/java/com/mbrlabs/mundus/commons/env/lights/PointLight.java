package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.math.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PointLight extends BaseLight {
    private Vector3 position;
}
