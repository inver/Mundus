package com.mbrlabs.mundus.editor.ui.modules.toolbar;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.events.AssetImportEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.importer.ImportModelDialog;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.importer.ImportTextureDialog;
import com.mbrlabs.mundus.editor.utils.Toaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolbarPresenter {

    private final AppUi appUi;
    private final EditorAssetManager assetManager;
    private final ImportModelDialog importModelDialog;
    private final ImportTextureDialog importTextureDialog;
    private final Toaster toaster;
    private final EventBus eventBus;

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
