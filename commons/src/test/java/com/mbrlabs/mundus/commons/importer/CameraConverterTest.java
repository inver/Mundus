package com.mbrlabs.mundus.commons.importer;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import org.junit.Assert;
import org.junit.Test;

public class CameraConverterTest {

    private final CameraConverter cameraConverter = new CameraConverter();

    @Test
    public void testConversion() {
        var cam = new PerspectiveCamera();
        cam.direction.set(1, 1, 1);
        cam.position.set(2, 2, 2);
        cam.far = 122;
        cam.near = 2222;

        var dto = cameraConverter.fromCamera(cam);
        var newCam = cameraConverter.fromDto(dto);

        Assert.assertEquals(cam.far, newCam.far, 0.0000001);
        Assert.assertEquals(cam.near, newCam.near, 0.0000001);
        Assert.assertEquals(cam.position, newCam.position);
        Assert.assertEquals(cam.direction, newCam.direction);
    }
}
