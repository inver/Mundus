package com.mbrlabs.mundus.editor.ui.modules.inspector.components.transform;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.ComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.outline.IdNode;
import com.mbrlabs.mundus.editor.ui.widgets.FloatField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
public class TransformWidgetPresenter implements UiComponentPresenter<UiComponentWidget> {

    private final EditorCtx ctx;
    private final EventBus eventBus;

    @Override
    public void init(UiComponentWidget uiComponent) {
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event ->
                uiComponent.setVisible(event.getEntityId() != IdNode.RootNode.ROOT_NODE_ID)
        );
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            if (!ctx.entityExists(event.getEntityId())) {
                return;
            }

            uiComponent.getContentTable().setEntityId(event.getEntityId());
            var position = ctx.getComponentByEntityId(event.getEntityId(), PositionComponent.class);

            uiComponent.getField("posX", FloatField.class).setValue(position.getLocalPosition().x);
            uiComponent.getField("posY", FloatField.class).setValue(position.getLocalPosition().y);
            uiComponent.getField("posZ", FloatField.class).setValue(position.getLocalPosition().z);

            uiComponent.getField("rotX", FloatField.class).setValue(position.getLocalRotation().x);
            uiComponent.getField("rotY", FloatField.class).setValue(position.getLocalRotation().y);
            uiComponent.getField("rotZ", FloatField.class).setValue(position.getLocalRotation().z);

            uiComponent.getField("sclX", FloatField.class).setValue(position.getLocalScale().x);
            uiComponent.getField("sclY", FloatField.class).setValue(position.getLocalScale().y);
            uiComponent.getField("sclZ", FloatField.class).setValue(position.getLocalScale().z);
        });

        addChangeListener(uiComponent, "posX", (pos, value) -> {
            var localPosition = pos.getLocalPosition();
            pos.getLocalPosition().set(value, localPosition.y, localPosition.z);
        });
        addChangeListener(uiComponent, "posY", (pos, value) -> {
            var localPosition = pos.getLocalPosition();
            pos.getLocalPosition().set(localPosition.x, value, localPosition.z);
        });
        addChangeListener(uiComponent, "posZ", (pos, value) -> {
            var localPosition = pos.getLocalPosition();
            pos.getLocalPosition().set(localPosition.x, localPosition.y, value);
        });
        addChangeListener(uiComponent, "rotX", (pos, value) -> {
            var localRotation = pos.getLocalRotation();
            pos.getLocalRotation().setEulerAngles(value, localRotation.y, localRotation.z);
        });
        addChangeListener(uiComponent, "rotY", (pos, value) -> {
            var localRotation = pos.getLocalRotation();
            pos.getLocalRotation().setEulerAngles(localRotation.x, value, localRotation.z);
        });
        addChangeListener(uiComponent, "rotZ", (pos, value) -> {
            var localRotation = pos.getLocalRotation();
            pos.getLocalRotation().setEulerAngles(localRotation.x, localRotation.y, value);
        });
        addChangeListener(uiComponent, "sclX", (pos, value) -> {
            var localScale = pos.getLocalScale();
            pos.getLocalScale().set(value, localScale.y, localScale.z);
        });
        addChangeListener(uiComponent, "sclY", (pos, value) -> {
            var localScale = pos.getLocalScale();
            pos.getLocalScale().set(localScale.x, value, localScale.z);
        });
        addChangeListener(uiComponent, "sclZ", (pos, value) -> {
            var localScale = pos.getLocalScale();
            pos.getLocalScale().set(localScale.x, localScale.y, value);
        });
    }

    private void addChangeListener(UiComponentWidget uiComponent, String fieldName,
                                   final BiConsumer<PositionComponent, Float> positionAction) {
        var field = uiComponent.getField(fieldName, FloatField.class);
        if (field == null) {
            return;
        }
        field.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var position = ctx.getComponentByEntityId(uiComponent.getContentTable().getEntityId(),
                        PositionComponent.class);

                Float v = field.getValue();
                if (v != null) {
                    positionAction.accept(position, v);
                }
            }
        });
    }

}
