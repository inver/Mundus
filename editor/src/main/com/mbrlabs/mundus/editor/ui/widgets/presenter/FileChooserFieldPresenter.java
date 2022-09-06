package com.mbrlabs.mundus.editor.ui.widgets.presenter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileChooserFieldPresenter {
    private final AppUi appUi;
    private final FileChooser fileChooser;

    public void initFileChooserField(FileChooserField field) {
        field.getFcBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fileChooser.setSelectionMode(field.getMode());
                fileChooser.setListener(new SingleFileChooserListener() {
                    @Override
                    protected void selected(FileHandle file) {
                        field.setValue(file);
                    }
                });
                appUi.addActor(fileChooser.fadeIn());
            }
        });
    }
}
