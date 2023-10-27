package com.mbrlabs.mundus.editor.ui.widgets;

import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiField;
import com.mbrlabs.mundus.editor.utils.StringUtils;

public class FloatField extends VisTextField implements UiField<Float> {

    public FloatField() {
        setTextFieldFilter(new FloatDigitsOnlyFilter(true));
    }

    @Override
    public void setValue(Float input) {
        setText(StringUtils.formatFloat(input, 3));
    }

    @Override
    public Float getValue() {
        return Float.parseFloat(getText());
    }
}
