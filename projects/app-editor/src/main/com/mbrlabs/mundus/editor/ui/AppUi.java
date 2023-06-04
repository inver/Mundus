package com.mbrlabs.mundus.editor.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class AppUi extends Stage {

    @Getter
    private final RenderWidget sceneWidget;

    public AppUi() {
        super(new ScreenViewport());
//        sceneWidget = new RenderWidget((ScreenViewport) getViewport());
        sceneWidget = new RenderWidget(this);
    }

    public ClickListener createOpenDialogListener(VisDialog dialog) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AppUi.this.showDialog(dialog);
            }
        };
    }

    public void showDialog(VisDialog dialog) {
        dialog.show(this);
    }

    @Override
    public void act() {
        super.act();
    }
}
