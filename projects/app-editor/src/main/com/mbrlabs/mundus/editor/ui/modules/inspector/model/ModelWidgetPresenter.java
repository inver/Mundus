package com.mbrlabs.mundus.editor.ui.modules.inspector.model;

import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import com.mbrlabs.mundus.commons.model.ModelObject;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.dsl.UiDslCreator;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.material.MaterialWidgetPresenter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelWidgetPresenter implements UiComponentPresenter<UiComponentWidget> {
    private final EditorCtx ctx;
    private final AssetManager assetManager;
    private final EventBus eventBus;
    private final UiDslCreator uiDslCreator;
    private final MaterialWidgetPresenter materialWidgetPresenter;

    public void setValues(@NotNull ModelComponentWidget widget) {
        var renderComponent = ctx.getComponentByEntityId(widget.getEntityId(), RenderComponent.class);
        var asset = assetManager.loadCurrentProjectAsset(
                ((RenderableObjectDelegate) renderComponent.getRenderable()).getAsset().getAssetName());
        widget.getAssetField().setAsset(asset);
    }

    @Override
    public void init(UiComponentWidget uiComponent) {
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            if (!ctx.isEntitySelected()) {
                uiComponent.setVisible(false);
                return;
            }

            var typeComponent = ctx.getComponentByEntityId(event.getEntityId(), TypeComponent.class);
            uiComponent.setVisible(typeComponent != null && typeComponent.getType() == TypeComponent.Type.OBJECT);
            uiComponent.setEntityId(event.getEntityId());

            uiComponent.getActor().clearChildren();

            if (typeComponent != null && typeComponent.getType() != TypeComponent.Type.OBJECT) {
                return;
            }
            var model = ctx.getCurrent().getRenderableObject(ModelObject.class, event.getEntityId());
            model.getModel().getMaterials().forEach((name, material) -> {
                UiComponentWidget matComponent = uiDslCreator.create(
                        "com/mbrlabs/mundus/editor/ui/modules/inspector/material/MaterialWidget.groovy"
                );

                materialWidgetPresenter.setMaterial(matComponent, name, material);
                uiComponent.getActor().add(matComponent.getActor()).top().left().expandX().row();
            });
        });

    }
}
