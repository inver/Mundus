package com.mbrlabs.mundus.editor.core.project;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Used as singleton current state of editor
 */
@Slf4j
public class EditorCtx implements Disposable {

    private ProjectContext current;
    private Viewport viewport;
    private final Map<AssetKey, Asset<?>> editorAssets = new HashMap<>();

    private int selectedEntityId = -1;

    public EditorCtx() {
//        mainCamera.near = 0.2f;
//        mainCamera.far = 10000;
//        mainCamera.rotateAround(Vector3.Zero, Vector3.X, -30);
//        mainCamera.rotateAround(Vector3.Zero, Vector3.Y, 45);
//
//        var tmp = new Vector3();
//        tmp.set(mainCamera.direction).nor().scl(-30);
//        mainCamera.position.add(tmp);
    }

    public Entity getSelectedEntity() {
        if (selectedEntityId < 0) {
            return null;
        }
        return current.getCurrentScene().getWorld().getEntity(selectedEntityId);
    }

    public int getSelectedEntityId() {
        return selectedEntityId;
    }

    public void selectEntity(int selectedEntityId) {
        this.selectedEntityId = selectedEntityId;
    }

    public ProjectContext getCurrent() {
        return current;
    }

    public void setCurrent(ProjectContext current) {
        this.current = current;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }


    public Map<AssetKey, Asset<?>> getAssetLibrary() {
        return editorAssets;
    }

    @Override
    public void dispose() {
        //todo
    }

    public World getCurrentWorld() {
        return getCurrent().getCurrentScene().getWorld();
    }

    @SuppressWarnings("unchecked")
    public <T extends Asset<?>> T getAsset(AssetKey key) {
        return (T) editorAssets.get(key);
    }


}

