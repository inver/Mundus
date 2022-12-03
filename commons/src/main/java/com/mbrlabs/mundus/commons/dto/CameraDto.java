package com.mbrlabs.mundus.commons.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CameraDto extends ComponentDto {
    private Vector3Dto position;
    private Vector3Dto viewPointPosition;
    // if directionGameObjectId >=0 -> this camera should be directed to game object with specified id
    private int directionGameObjectId;
    private float near;
    private float far;
}
