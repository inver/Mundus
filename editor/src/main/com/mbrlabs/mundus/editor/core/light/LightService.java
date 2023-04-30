package com.mbrlabs.mundus.editor.core.light;

import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.LightComponent;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.editor.core.ecs.EditorEcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.ui.components.light.LightRenderDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LightService {
    private final EditorCtx ctx;
    private final EditorEcsService editorEcsService;

    public HierarchyNode createDirectionLight() {
        return editorEcsService.createEntityWithDirection(ctx.getCurrentWorld(), new Vector3(0, 10f, 0),
                Vector3.Zero, "Direction light", new LightRenderDelegate(), new LightComponent()
        );
    }
}
