package com.mbrlabs.mundus.commons.importer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.dto.CameraDto;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.CameraComponent;

public class CameraConverter {

    protected CameraComponent toComponent(GameObject go, CameraDto dto) {
        return new CameraComponent(go, fromDto(dto));
    }

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

    public CameraDto fromComponent(CameraComponent c) {
        var res = fromCamera(c.getInstance());
        if (c.getGoId() >= 0) {
            res.setViewPointPosition(null);
            res.setDirectionGameObjectId(c.getGoId());
        }
        return res;
    }

    public void addComponents(GameObject go, CameraDto dto) {
        go.addComponent(new CameraComponent(go, fromDto(dto)));
    }
}
