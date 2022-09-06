package com.mbrlabs.mundus.editor.ui.modules.menubar;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mbrlabs.mundus.editor.appUimodules.menu.FileMenu;
import com.mbrlabs.mundus.editor.core.project.ProjectAlreadyImportedException;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.project.ProjectOpenException;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.*;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.importer.ImportModelDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.settings.SettingsDialog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MenuBarPresenter {

    private final AppUi appUi;
    private final ProjectManager projectManager;
    private final NewProjectDialog newProjectDialog;
    private final ExitDialog exitDialog;
    private final FileChooser fileChooser;
    private final CommandHistory commandHistory;
    private final ImportModelDialog importModelDialog;
    private final AmbientLightDialog ambientLightDialog;
    private final SkyboxDialog skyboxDialog;
    private final FogDialog fogDialog;
    private final IBLBoxDialog iblBoxDialog;
    private final SettingsDialog settingsDialog;

    @Nullable
    public EventListener newProjectListener() {
        return appUi.createOpenDialogListener(newProjectDialog);
    }

    @Nullable
    public EventListener importProjectListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fileChooser.setListener(new SingleFileChooserListener() {
                    @Override
                    protected void selected(FileHandle file) {
                        try {
                            projectManager.changeProject(projectManager.importProject(file.path()));
                        } catch (ProjectAlreadyImportedException e) {
                            log.warn("This Project is already imported.");
                            Dialogs.showErrorDialog(appUi, "This Project is already imported.");
                        } catch (ProjectOpenException e) {
                            log.warn("This Project can't be opened.");
                            Dialogs.showErrorDialog(appUi, "This Project can't be opened.");
                        }
                    }
                });
            }
        };
    }

    @Nullable
    public EventListener exitListener() {
        return appUi.createOpenDialogListener(exitDialog);
    }

    @NotNull
    public FileMenu.RecentProjectListener recentProjectListener() {
        return project -> {
            try {
                projectManager.changeProject(projectManager.loadProject(project));
            } catch (Exception e) {
                log.error("ERROR", e);
                Dialogs.showErrorDialog(appUi, "Could not open project");
            }
        };
    }

    @Nullable
    public EventListener redoListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandHistory.goForward();
            }
        };
    }

    @Nullable
    public EventListener undoListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandHistory.goBack();
            }
        };
    }

    @Nullable
    public EventListener importMeshListener() {
        return appUi.createOpenDialogListener(importModelDialog);
    }

    @Nullable
    public EventListener addAmbilentLight() {
        return appUi.createOpenDialogListener(ambientLightDialog);
    }

    @Nullable
    public EventListener addSkyBox() {
        return appUi.createOpenDialogListener(skyboxDialog);
    }

    @Nullable
    public EventListener addIblImage() {
        return appUi.createOpenDialogListener(iblBoxDialog);
    }

    @Nullable
    public EventListener addFog() {
        return appUi.createOpenDialogListener(fogDialog);
    }

    @Nullable
    public EventListener windowsSettingsListener() {
        return appUi.createOpenDialogListener(settingsDialog);
    }
}
