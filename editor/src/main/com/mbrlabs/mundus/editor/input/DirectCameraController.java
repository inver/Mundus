package com.mbrlabs.mundus.editor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import org.springframework.stereotype.Component;

@Component
public class DirectCameraController extends InputAdapter {

    private static final float DEFAULT_ZOOM_SPEED = 10f;
    private static final float DEFAULT_ROTATION_SPEED = 0.5f;
    private static final float DEFAULT_MOVE_SPEED = 0.5f;
    private float zoomAmount = DEFAULT_ZOOM_SPEED;
    private float degreesPerPixel = DEFAULT_ROTATION_SPEED;
    private float floatsPerPixel = DEFAULT_MOVE_SPEED;

    private final Vector3 pointOfDirection = new Vector3();
    private final Vector3 up = new Vector3(0, 1, 0);
    ;
    private final Vector3 tmpVector = new Vector3();
    private Camera current;

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (current != null) {
            tmpVector.set(current.direction).nor().scl(-amountY * zoomAmount).add(current.position);
            current.position.set(tmpVector);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (current == null) {
            return false;
        }

        var deltaX = -Gdx.input.getDeltaX();
        var deltaY = -Gdx.input.getDeltaY();
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            deltaX *= degreesPerPixel;
            deltaY *= degreesPerPixel;

            current.rotateAround(pointOfDirection, up, deltaX);
            tmpVector.set(current.direction).crs(up).nor();
            current.rotateAround(pointOfDirection, tmpVector, deltaY);

            return true;
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            deltaX *= floatsPerPixel;
            deltaY *= floatsPerPixel;

            current.position.add(deltaX, 0, deltaY);
            pointOfDirection.add(deltaX, 0, deltaY);

            return true;
        }


        return false;
    }

    public void setCurrent(Camera current) {
        this.current = current;
    }
}
