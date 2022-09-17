package com.mbrlabs.mundus.editor.ui.modules.dialogs.settings;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.project.ProjectStorage;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent;
import com.mbrlabs.mundus.editor.ui.widgets.presenter.FileChooserFieldPresenter;
import com.mbrlabs.mundus.editor.utils.Toaster;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettingsDialogPresenter {
    private final Registry registry;
    private final FileChooserFieldPresenter fileChooserFieldPresenter;
    private final ProjectStorage projectStorage;
    private final EventBus eventBus;
    private final Toaster toaster;
    private final ProjectManager projectManager;

    public void initGeneralSettings(@NotNull GeneralSettingsTable table) {
        table.getKeyboardLayouts().setSelected(registry.getSettings().getKeyboardLayout());
        table.getKeyboardLayouts().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                registry.getSettings().setKeyboardLayout(table.getKeyboardLayouts().getSelected());
            }
        });
        fileChooserFieldPresenter.initFileChooserField(table.getFbxBinary());
        table.getFbxBinary().setText(registry.getSettings().getFbxConvBinary());
        table.setSaveListener(() -> {
            registry.getSettings().setFbxConvBinary(table.getFbxBinary().getPath());
            projectStorage.saveRegistry(registry);
            eventBus.post(new SettingsChangedEvent(registry.getSettings()));
            toaster.success("Settings saved");
        });
    }

    public void initExportSettings(@NotNull ExportSettingsTable table) {
        eventBus.register(table);
        fileChooserFieldPresenter.initFileChooserField(table.getFileChooserField());
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            var settings = projectManager.getCurrent().settings;
            if (settings != null && settings.getExport() != null) {
                var exportSettings = settings.getExport();
                if (exportSettings.outputFolder != null) {
                    table.getFileChooserField().setText(exportSettings.outputFolder.path());
                }
                table.getAllAssets().setChecked(exportSettings.allAssets);
                table.getCompression().setChecked(exportSettings.compressScenes);
                table.getJsonType().setSelected(exportSettings.jsonType);
            }
        });
        table.setSaveListener(() -> {
            var settings = projectManager.getCurrent().settings;
            if (settings == null || settings.getExport() == null) {
                return;
            }
            var exportSettings = settings.getExport();
            exportSettings.allAssets = table.getAllAssets().isChecked();
            exportSettings.compressScenes = table.getCompression().isChecked();
            exportSettings.jsonType = table.getJsonType().getSelected();
            exportSettings.outputFolder = new FileHandle(table.getFileChooserField().getPath());

            projectStorage.saveProjectContext(projectManager.getCurrent());
            toaster.success("Settings saved");
        });

    }
}
