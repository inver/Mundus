package com.mbrlabs.mundus.editor.ui.components.camera;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.action.ActionComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.ui.components.EditorHandleComponent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CameraService {
    private final EditorCtx ctx;

    public GameObject createCamera() {
        var id = ctx.getCurrent().obtainID();
        //todo check selected game object
        var root = new GameObject("Camera " + id, id);
        root.addComponent(new ActionComponent(root, new LookAtAction()));

        id = ctx.getCurrent().obtainID();
        var handleGO = new GameObject("'Look at' handle", id);
        handleGO.translate(0, 0, 10.2f);
        handleGO.addComponent(new EditorHandleComponent(handleGO));
        handleGO.addComponent(new ActionComponent(handleGO, new CameraHandleLineAction()));
        root.addChild(handleGO);

        id = ctx.getCurrent().obtainID();
        var cameraGO = new GameObject("'Camera' handle ", id);
        cameraGO.addComponent(new EditorCameraComponent(cameraGO, new PerspectiveCamera()));
        root.addChild(cameraGO);

        ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(root);
        return root;
    }
}
