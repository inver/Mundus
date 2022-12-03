package com.mbrlabs.mundus.editor.core.project;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Renderable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used as singleton current state of editor
 */
@Component
public class EditorCtx implements Disposable {

    private ProjectContext current;
    private Viewport viewport;
    private final Map<String, Asset<?>> assetLibrary = new HashMap<>();

    private GameObject selected = null;

    private final Camera mainCamera = new PerspectiveCamera();
    private Camera selectedCamera = null;

    private final List<Renderable> editorComponents = new ArrayList<>();

    public EditorCtx() {
        mainCamera.near = 0.2f;
        mainCamera.far = 10000;
        mainCamera.rotateAround(Vector3.Zero, Vector3.X, -30);
        mainCamera.rotateAround(Vector3.Zero, Vector3.Y, 45);

        var tmp = new Vector3();
        tmp.set(mainCamera.direction).nor().scl(-30);
        mainCamera.position.add(tmp);
    }

    public GameObject getSelected() {
        return selected;
    }

    public void setSelected(GameObject selected) {
        this.selected = selected;
    }

    public ProjectContext getCurrent() {
        return current;
    }

    public void setCurrent(ProjectContext current) {
        this.current = current;
    }

    public Camera getCamera() {
        if (selectedCamera == null) {
            return mainCamera;
        }

        if (current == null || current.getCurrentScene() == null
                || CollectionUtils.isEmpty(current.getCurrentScene().getCameras())) {
            return null;
        }
        return current.getCurrentScene().getCameras().get(0);
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }


    public Map<String, Asset<?>> getAssetLibrary() {
        return assetLibrary;
    }

    @Override
    public void dispose() {
        //todo
    }
}
