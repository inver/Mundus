package com.mbrlabs.mundus.editor.ui.widgets.dsl;

import com.mbrlabs.mundus.editor.ui.widgets.FloatField;

public class UiFloatField extends UiComponent<FloatField> {
    public UiFloatField() {
        super(new FloatField());
    }

    public void setWidth(float value) {
        actor.setWidth(value);
    }
}
