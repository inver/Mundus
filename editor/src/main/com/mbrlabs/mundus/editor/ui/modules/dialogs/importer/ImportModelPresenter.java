package com.mbrlabs.mundus.editor.ui.modules.dialogs.importer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import com.mbrlabs.mundus.commons.model.ModelFiles;
import com.mbrlabs.mundus.editor.core.assets.EditorModelService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.utils.Toaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportModelPresenter {

    private final EditorCtx ctx;
    private final ProjectManager projectManager;
    private final Toaster toaster;
    private final EditorModelService editorModelService;
    private final EventBus eventBus;
    private final ModelImporter modelImporter;
    private final Registry registry;

    public void initImportButton(@NotNull ImportModelWidget importModelWidget, Runnable closeRunnable) {
        importModelWidget.getImportBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (importModelWidget.getPreviewInstance() == null) {
                    toaster.error("There is nothing to import");
                    return;
                }

                try {
                    var node = editorModelService.createTerrainEntity(importModelWidget.getImportedModel());
                    ctx.getCurrent().getCurrentScene().getRootNode().addChild(node);

                    projectManager.saveProject(ctx.getCurrent());
                    //todo is needed import event here?
//                    eventBus.post(new AssetImportEvent(asset));
                    eventBus.post(new SceneGraphChangedEvent());
                    //todo is it needed?
//                    eventBus.post(new AssetImportEvent(asset));
                    toaster.success("Model imported");
                } catch (Exception e) {
                    log.error("ERROR", e);
                    toaster.error("Error while import model. See logs, please.");
                }

                importModelWidget.dispose();
                closeRunnable.run();
            }
        });
    }

    @Nullable
    public ModelFiles importModelFromFile(@Nullable FileHandle fileHandle) {
        var tmpFolder = registry.createTempFolder();
        //todo rework to import with convert to asset structure
        var res = modelImporter.importAndConvertToTmpFolder(tmpFolder, fileHandle);
        return res;
    }

    @NotNull
    public Model loadModel(@NotNull ModelFiles importedModel) {
        //todo rework to load from asset
        return modelImporter.loadModel(importedModel.getMain());
    }
}
