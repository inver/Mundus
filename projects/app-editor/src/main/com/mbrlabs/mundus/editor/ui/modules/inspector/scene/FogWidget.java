package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.env.fog.Fog;
import com.mbrlabs.mundus.editor.config.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerField;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class FogWidget extends BaseInspectorWidget {

    private final VisCheckBox enabled = new VisCheckBox("");
    private final VisTextField density = new VisTextField("0");
    private final VisTextField gradient = new VisTextField("0");
    private final ColorPickerField colorPickerField = new ColorPickerField();


    public FogWidget(@NotNull UiComponentHolder uiComponentHolder) {
        super(uiComponentHolder, "Fog");

        addFormField(getCollapsibleContent(), "Enabled", enabled, false);
        addFormField(getCollapsibleContent(), "Density", density);
        addFormField(getCollapsibleContent(), "Gradient", gradient);
        addFormField(getCollapsibleContent(), "Color", colorPickerField);
    }

    public void resetValues(boolean enabled, Fog fog) {
        this.enabled.setChecked(enabled);
        density.setText(fog.getDensity() + "");
        gradient.setText(fog.getGradient() + "");
        colorPickerField.setSelectedColor(fog.getColor());
    }
}
