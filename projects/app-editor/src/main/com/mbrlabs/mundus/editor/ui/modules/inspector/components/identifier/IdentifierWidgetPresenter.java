package com.mbrlabs.mundus.editor.ui.modules.inspector.components.identifier;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.core.ecs.component.NameComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.widgets.UiFormTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentifierWidgetPresenter implements UiComponentPresenter<UiFormTable> {

    private final EditorCtx ctx;
    private final EventBus eventBus;

    @Override
    public void init(UiFormTable table) {
        var nameField = (VisTextField) table.getField("name");
        var activeField = (VisCheckBox) table.getField("active");

        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            if (!ctx.entityExists(event.getEntityId())) {
                return;
            }

            table.setEntityId(event.getEntityId());
            var name = ctx.getComponentByEntityId(event.getEntityId(), NameComponent.class);

            nameField.setText(name.getName());
            activeField.setChecked(ctx.getCurrentWorld().getEntity(event.getEntityId()).isActive());
        });

        nameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ctx.getComponentByEntityId(table.getEntityId(), NameComponent.class).setName(nameField.getText());
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
