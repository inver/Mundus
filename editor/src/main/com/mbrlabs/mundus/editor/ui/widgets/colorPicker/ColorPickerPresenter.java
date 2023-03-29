package com.mbrlabs.mundus.editor.ui.widgets.colorPicker;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mbrlabs.mundus.editor.config.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.AppUi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ColorPickerPresenter {

    private final AppUi appUi;
    private final UiComponentHolder widgetsHolder;

    public void init(ColorPickerField field) {
        var colorPicker = widgetsHolder.getColorPicker();

        field.getCpBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                colorPicker.setColor(field.getColor());
                colorPicker.setListener(field.getColorPickerListenerInternal());
                appUi.addActor(colorPicker.fadeIn());
            }
        });
    }
}
