package com.mbrlabs.mundus.editor.ui.modules.dialogs;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.widgets.ImageChooserField;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class IBLBoxDialog extends BaseDialog
        implements ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener {

    private final ImageChooserField iblImageField;
    private final VisTextButton applyButton = new VisTextButton("Apply");

    public IBLBoxDialog(EventBus eventBus, AppUi appUi, FileChooser fileChooser) {
        super("IBLBox");

        iblImageField = new ImageChooserField(300);

        eventBus.register(this);
        setupUI();
    }

    private void setupUI() {
        iblImageField.setButtonText("IBL Image");

        var root = new VisTable();
//        root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f);

        add(root).left().top();

        root.add(iblImageField).row();
        root.add(applyButton).padTop(6f).row();

    }

    @Override
    public void onProjectChanged(@NotNull ProjectChangedEvent event) {

    }

    @Override
    public void onSceneChanged(@NotNull SceneChangedEvent event) {

    }
}
