package com.mbrlabs.mundus.editor.ui.components.camera;

import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.CameraComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.editor.core.ecs.EditorEcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CameraService {
    private final EditorCtx ctx;
    private final EditorEcsService editorEcsService;

    public int createEcsCamera(int parentId) {
        var world = ctx.getCurrent().getCurrentScene().getWorld();

        return editorEcsService.createEntityWithDirection(world, parentId, new Vector3(0, 0, 10.2f), Vector3.Zero,
                "Camera", new CameraBodyRenderDelegate(), new CameraComponent(),
                new TypeComponent(TypeComponent.Type.CAMERA)
        );
    }
}
