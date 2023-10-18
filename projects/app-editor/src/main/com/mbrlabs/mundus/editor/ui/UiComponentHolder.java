package com.mbrlabs.mundus.editor.ui;

import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.mbrlabs.mundus.editor.ui.widgets.ButtonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Holder uses for some widgets, according VIS ui life circle
 */
@Component
@RequiredArgsConstructor
public class UiComponentHolder {
    private final ColorPicker colorPicker = new ColorPicker();
    private final Separator.SeparatorStyle separatorStyle =
            new Separator.SeparatorStyle(VisUI.getSkin().getDrawable("mundus-separator-green"), 1);
    private final ButtonFactory buttonFactory;

    public void init() {
//        colorPicker = new ColorPicker();
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public Separator.SeparatorStyle getSeparatorStyle() {
        return separatorStyle;
    }

    public ButtonFactory getButtonFactory() {
        return buttonFactory;
    }
}
