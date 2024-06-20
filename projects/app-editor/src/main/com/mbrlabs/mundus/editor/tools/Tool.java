package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableObject;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Tool extends InputAdapter implements RenderableObject, Disposable {
    protected final EditorCtx ctx;
    protected final String shaderKey;
    protected final CommandHistory history;
    private final String name;

    public String getName() {
        return name;
    }

    public abstract SymbolIcon getIcon();

    public void act() {

    }

    public abstract void onActivated();

    public abstract void onDisabled();

    protected PositionComponent getPositionOfSelectedEntity() {
        return ctx.getComponentByEntityId(ctx.getSelectedEntityId(), PositionComponent.class);
    }
}
