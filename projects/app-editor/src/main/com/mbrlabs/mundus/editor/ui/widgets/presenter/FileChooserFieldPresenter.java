package com.mbrlabs.mundus.editor.ui.widgets.presenter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.ImageChooserField;
import com.mbrlabs.mundus.editor.utils.FileFormatUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileChooserFieldPresenter {
    private final AppUi appUi;
    private final FileChooser fileChooser;

    public void initFileChooserField(FileChooserField field, FileChooserField.FileSelectedListener fileSelected) {
        initFileChooserField(field);
        field.setCallback(fileSelected);
    }

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

    public void initImageChooserField(@NotNull final ImageChooserField field) {
        field.getSelectButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
                fileChooser.setListener(new SingleFileChooserListener() {
                    public void selected(FileHandle file) {
                        if (FileFormatUtils.isImage(file)) {
                            field.setImage(file);
                        } else {
                            Dialogs.showErrorDialog(appUi, "This is no image");
                        }
                    }
                });
                appUi.addActor(fileChooser.fadeIn());
            }
        });
    }
}
