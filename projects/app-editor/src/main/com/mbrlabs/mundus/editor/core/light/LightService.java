package com.mbrlabs.mundus.editor.core.light;

import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.LightComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.editor.core.ecs.EcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.ui.components.light.LightRenderDelegate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LightService {
    private final EditorCtx ctx;
    private final EcsService ecsService;

    public int createDirectionLight(int parentId) {
        return ecsService.createEntityWithDirection(ctx.getCurrentWorld(), parentId, new Vector3(0, 10f, 0),
                Vector3.Zero, "Direction light", new LightRenderDelegate(), new LightComponent(),
                new TypeComponent(TypeComponent.Type.LIGHT)
        );
    }
}
