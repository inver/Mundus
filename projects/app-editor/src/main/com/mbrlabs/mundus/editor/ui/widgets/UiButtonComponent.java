package com.mbrlabs.mundus.editor.ui.widgets;

import com.kotcrab.vis.ui.widget.VisTextButton;

public class UiButtonComponent extends UiComponent<VisTextButton> {
    public UiButtonComponent() {
        super(new VisTextButton(""));
    }

    public void setText(String text) {
        actor.setText(text);
    }
}
