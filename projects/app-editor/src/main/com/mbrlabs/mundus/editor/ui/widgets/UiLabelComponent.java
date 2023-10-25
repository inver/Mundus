package com.mbrlabs.mundus.editor.ui.widgets;

import com.kotcrab.vis.ui.widget.VisLabel;

public class UiLabelComponent extends UiComponent<VisLabel> {
    public UiLabelComponent() {
        super(new VisLabel());
    }

    public UiLabelComponent(String text, String layoutTypes) {
        this();
        setText(text);
        setLayoutTypes(layoutTypes);
    }

    public void setText(String text) {
        actor.setText(text);
    }
}
