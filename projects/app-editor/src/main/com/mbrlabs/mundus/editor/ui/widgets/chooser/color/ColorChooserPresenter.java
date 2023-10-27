package com.mbrlabs.mundus.editor.ui.widgets.chooser.color;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.AppUi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ColorChooserPresenter {

    private final AppUi appUi;
    private final UiComponentHolder uiComponentHolder;

    public void init(ColorChooserField field) {
        var colorPicker = uiComponentHolder.getColorPicker();

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
