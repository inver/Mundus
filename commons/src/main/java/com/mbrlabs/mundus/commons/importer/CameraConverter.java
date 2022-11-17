package com.mbrlabs.mundus.commons.importer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.dto.CameraDto;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;

public class CameraConverter {

    public static PerspectiveCamera fromDto(CameraDto dto) {
        var res = new PerspectiveCamera();
        res.position.set(dto.getPosition().toVector());
        res.direction.set(dto.getDirection().toVector());
        res.near = dto.getNear();
        res.far = dto.getFar();
        return res;
    }

    public static CameraDto fromCamera(Camera camera) {
        var res = new CameraDto();
        res.setDirection(new Vector3Dto(camera.direction));
        res.setPosition(new Vector3Dto(camera.position));
        res.setFar(camera.far);
        res.setNear(camera.near);
        return res;
    }
}
