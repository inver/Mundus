package com.mbrlabs.mundus.editor.ui.modules.inspector.components.model;

import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelComponentPresenter {
    private final EditorCtx ctx;
    private final AssetManager assetManager;
    private final EventBus eventBus;

    void init(ModelComponentWidget widget) {
//        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
//            var renderComponent = ctx.getComponentByEntityId(event.getEntityId(), RenderComponent.class);
//            var asset = assetManager.loadCurrentProjectAsset(
//                    ((RenderableObjectDelegate) renderComponent.getRenderable()).getAsset().getAssetName());
//            widget.resetValues(asset);
//        });
    }

    public void setValues(@NotNull ModelComponentWidget widget) {
        var renderComponent = ctx.getComponentByEntityId(widget.getEntityId(), RenderComponent.class);
        var asset = assetManager.loadCurrentProjectAsset(
                ((RenderableObjectDelegate) renderComponent.getRenderable()).getAsset().getAssetName());
        widget.getAssetField().setAsset(asset);
    }
}
