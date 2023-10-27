package com.mbrlabs.mundus.editor.ui.widgets.dsl;

import com.mbrlabs.mundus.editor.ui.widgets.Slider;

public class UiSlider extends UiComponent<Slider> {
    public UiSlider() {
        super(new Slider());
    }

    public void setMin(float min) {
        actor.getSlider().setRange(min, actor.getSlider().getMaxValue());
    }

    public void setMax(float max) {
        actor.getSlider().setRange(actor.getSlider().getMinValue(), max);
    }

    public void setStepSize(float stepSize) {
        actor.getSlider().setStepSize(stepSize);
    }
}
