package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.editor.config.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerField;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class AmbientLightWidget extends BaseInspectorWidget {

    private final VisCheckBox enabled = new VisCheckBox("");
    private final VisTextField intensity = new VisTextField("0");
    private final ColorPickerField colorPickerField = new ColorPickerField();

    public AmbientLightWidget(@NotNull UiComponentHolder uiComponentHolder) {
        super(uiComponentHolder, "Ambient light");

        addFormField(getCollapsibleContent(), "Enabled", enabled, false);
        addFormField(getCollapsibleContent(), "Intensity", intensity);
        addFormField(getCollapsibleContent(), "Color", colorPickerField);
    }

    public void resetValues(boolean enabled, BaseLight ambientLight) {
        this.enabled.setChecked(enabled);
        intensity.setText(ambientLight.getIntensity() + "");
        colorPickerField.setSelectedColor(ambientLight.getColor());
    }
}
