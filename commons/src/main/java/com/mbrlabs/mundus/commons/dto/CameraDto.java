package com.mbrlabs.mundus.commons.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CameraDto {
    private Vector3Dto position;
    private Vector3Dto direction;
    private float near;
    private float far;
}
