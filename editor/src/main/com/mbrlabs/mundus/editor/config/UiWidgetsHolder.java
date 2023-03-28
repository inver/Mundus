package com.mbrlabs.mundus.editor.config;

import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import org.springframework.stereotype.Component;

/**
 * Holder uses for some widgets, according VIS ui life circle
 */
@Component
public class UiWidgetsHolder {
    private final ColorPicker colorPicker = new ColorPicker();
    private final Separator.SeparatorStyle separatorStyle =
            new Separator.SeparatorStyle(VisUI.getSkin().getDrawable("mundus-separator-green"), 1);

    public void init() {
//        colorPicker = new ColorPicker();
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public Separator.SeparatorStyle getSeparatorStyle() {
        return separatorStyle;
    }
}
