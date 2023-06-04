package com.mbrlabs.mundus.editor.ui.modules.toolbar;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.project.ProjectAlreadyImportedException;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.project.ProjectOpenException;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.AssetImportEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.AmbientLightDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.ExitDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.FogDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.IBLBoxDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.NewProjectDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.SkyboxDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.importer.ImportModelDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.importer.ImportTextureDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.settings.SettingsDialog;
import com.mbrlabs.mundus.editor.ui.modules.outline.ClickButtonListener;
import com.mbrlabs.mundus.editor.utils.Toaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppToolbarPresenter {

    private final AppUi appUi;
    private final EditorAssetManager assetManager;
    private final ImportModelDialog importModelDialog;
    private final ImportTextureDialog importTextureDialog;
    private final Toaster toaster;
    private final EventBus eventBus;
    private final NewProjectDialog newProjectDialog;
    private final ExitDialog exitDialog;
    private final Registry registry;
    private final ProjectManager projectManager;
    private final CommandHistory commandHistory;
    private final FileChooser fileChooser;
    private final AmbientLightDialog ambientLightDialog;
    private final SkyboxDialog skyboxDialog;
    private final IBLBoxDialog iblBoxDialog;
    private final FogDialog fogDialog;
    private final SettingsDialog settingsDialog;

    public void initToolbar(AppToolbar toolbar) {
        toolbar.getFileMenu().getNewProject().addListener(appUi.createOpenDialogListener(newProjectDialog));
        toolbar.getFileMenu().getImportProject().addListener(new ClickListener() {
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
        });
        toolbar.getFileMenu().getSaveProject()
                .addListener(new ClickButtonListener(projectManager::saveCurrentProject));
        initRecentProjectPopup(toolbar.getFileMenu().getRecentProjectsPopup());
        toolbar.getFileMenu().getExit().addListener(appUi.createOpenDialogListener(exitDialog));

        toolbar.getEditMenu().getRedo().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandHistory.goForward();
            }
        });
        toolbar.getEditMenu().getUndo().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commandHistory.goBack();
            }
        });

        toolbar.getEnvironmentMenu().getAmbientLight().addListener(appUi.createOpenDialogListener(ambientLightDialog));
        toolbar.getEnvironmentMenu().getSkybox().addListener(appUi.createOpenDialogListener(skyboxDialog));
        toolbar.getEnvironmentMenu().getIblImage().addListener(appUi.createOpenDialogListener(iblBoxDialog));
        toolbar.getEnvironmentMenu().getFog().addListener(appUi.createOpenDialogListener(fogDialog));

        toolbar.getAssetsMenu().getImportMesh().addListener(importMeshListener());

        toolbar.getWindowMenu().getSettings().addListener(appUi.createOpenDialogListener(settingsDialog));
    }

    private void initRecentProjectPopup(PopupMenu recentProjectsPopup) {
        for (var ref : registry.getProjects()) {
            var menu = new MenuItem(ref.getName() + " - [" + ref.getPath() + "]");
            menu.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    try {
                        projectManager.changeProject(projectManager.loadProject(ref));
                    } catch (Exception e) {
                        log.error("ERROR", e);
                        Dialogs.showErrorDialog(appUi, "Could not open project");
                    }
                }
            });
            recentProjectsPopup.addItem(menu);
        }
    }

    @Nullable
    public EventListener importMeshListener() {
        return appUi.createOpenDialogListener(importModelDialog);
    }

    @Nullable
    public EventListener importTextureListener() {
        return appUi.createOpenDialogListener(importTextureDialog);
    }

    @Nullable
    public EventListener createMaterialListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialogs.showInputDialog(appUi, "Create new material", "Material name",
                        new InputDialogAdapter() {
                            @Override
                            public void finished(String input) {
                                try {
                                    var material = assetManager.createMaterialAsset(input);
                                    eventBus.post(new AssetImportEvent(material));
                                } catch (Exception e) {
                                    log.error("ERROR", e);
                                    toaster.error(e.toString());
                                }
                            }
                        });
            }
        };
    }
}
