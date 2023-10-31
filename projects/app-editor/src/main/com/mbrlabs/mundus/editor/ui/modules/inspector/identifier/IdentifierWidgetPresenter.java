package com.mbrlabs.mundus.editor.ui.modules.inspector.identifier;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.core.ecs.component.NameComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.outline.IdNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentifierWidgetPresenter implements UiComponentPresenter<UiComponentWidget> {

    private final EditorCtx ctx;
    private final EventBus eventBus;

    @Override
    public void init(UiComponentWidget widget) {
        var nameField = (VisTextField) widget.getField("name");
        var activeField = (VisCheckBox) widget.getField("active");

        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event ->
                widget.setVisible(ctx.isEntitySelected())
        );
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            if (!ctx.entityExists(event.getEntityId())) {
                return;
            }

            widget.getContentTable().setEntityId(event.getEntityId());
            var name = ctx.getComponentByEntityId(event.getEntityId(), NameComponent.class);

            nameField.setText(name.getName());
            activeField.setChecked(ctx.getCurrentWorld().getEntity(event.getEntityId()).isActive());
        });

        nameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ctx.getComponentByEntityId(widget.getContentTable().getEntityId(), NameComponent.class)
                        .setName(nameField.getText());
            }
        });
        activeField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //todo
//                ctx.getCurrentWorld().getEntity(table.getEntityId()).isActive()
            }
        });
    }
}
