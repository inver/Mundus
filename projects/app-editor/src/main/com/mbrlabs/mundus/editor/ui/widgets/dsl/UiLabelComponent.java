package com.mbrlabs.mundus.editor.ui.widgets.dsl;

import com.kotcrab.vis.ui.widget.VisLabel;

public class UiLabelComponent extends UiComponent<VisLabel> {
    public UiLabelComponent() {
        super(new VisLabel());
    }

    public UiLabelComponent(String text, String layoutModifiers) {
        this();
        setText(text);
        layout(layoutModifiers);
    }

    public void setText(String text) {
        actor.setText(text);
    }
}
