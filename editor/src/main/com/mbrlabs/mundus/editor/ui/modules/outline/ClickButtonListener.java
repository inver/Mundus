package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClickButtonListener extends ClickListener {

    private final Runnable action;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        action.run();
    }
}
