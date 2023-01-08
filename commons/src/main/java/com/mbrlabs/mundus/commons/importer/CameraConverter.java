package com.mbrlabs.mundus.commons.importer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.dto.CameraDto;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;

public class CameraConverter {

    public PerspectiveCamera fromDto(CameraDto dto) {
        var res = new PerspectiveCamera();
        res.position.set(dto.getPosition().toVector());
        res.direction.set(dto.getViewPointPosition().toVector());
        res.near = dto.getNear();
        res.far = dto.getFar();
        return res;
    }

    public CameraDto fromCamera(Camera camera) {
        var res = new CameraDto();
        res.setViewPointPosition(new Vector3Dto(camera.direction));
        res.setPosition(new Vector3Dto(camera.position));
        res.setFar(camera.far);
        res.setNear(camera.near);
        return res;
    }

}
