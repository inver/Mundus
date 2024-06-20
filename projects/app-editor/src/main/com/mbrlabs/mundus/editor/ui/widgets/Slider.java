package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import lombok.Getter;

import static com.mbrlabs.mundus.editor.utils.StringUtils.formatFloat;

public class Slider extends VisTable {

    @Getter
    private final VisSlider slider;
    private final VisLabel value = new VisLabel("0");

    public Slider() {
        super();
        slider = new VisSlider(0, 1, 0.1f, false);

        add(value).padRight(8f).left();
        add(slider).expandX().fillX().right();

        slider.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event != null) {
                    event.stop();
                }
                return true;
            }
        });
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                value.setText(String.format(formatFloat(slider.getValue(), 2)));
            }
        });
    }
}
