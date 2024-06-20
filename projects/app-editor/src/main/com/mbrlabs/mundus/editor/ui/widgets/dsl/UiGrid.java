package com.mbrlabs.mundus.editor.ui.widgets.dsl;

import com.kotcrab.vis.ui.layout.GridGroup;

public class UiGrid extends UiComponent<GridGroup> {

    public UiGrid() {
        super(new GridGroup());
    }

    @SuppressWarnings("unused")
    public void setItemSize(float size) {
        actor.setItemSize(size);
    }

    @SuppressWarnings("unused")
    public void setSpacing(float spacing) {
        actor.setSpacing(spacing);
    }
}
