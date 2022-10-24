package com.mbrlabs.mundus.editor.core.project;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Used as singleton current state of editor
 */
@Component
public class EditorCtx implements Disposable {

    private ProjectContext current;
    private Camera camera;
    private Viewport viewport;
    private final Map<String, Asset<?>> assetLibrary = new HashMap<>();

    private GameObject selected = null;

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
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
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
