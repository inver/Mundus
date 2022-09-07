package com.mbrlabs.mundus.editor.ui.modules.dock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.dock.assets.AssetLibraryViewer;
import com.mbrlabs.mundus.editor.ui.modules.dock.assets.AssetsDock;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DockBarPresenter {
    private final ProjectManager projectManager;
    private final EventBus eventBus;
    private final AppUi appUi;

    public void initAppAssets(@NotNull AssetLibraryViewer libraryAssets) {
        libraryAssets.getFilesView().clearChildren();
        var projectContext = projectManager.getCurrent();
        if (projectContext.assetManager == null) {
            return;
        }
        for (var asset : projectContext.assetManager.getAssets()) {
            var assetItem = new AssetLibraryViewer.AssetItem(asset);
            libraryAssets.getFilesView().addActor(assetItem);
            assetItem.addListener(new InputListener() {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (event.getButton() == Input.Buttons.RIGHT) {
                        libraryAssets.setSelected(assetItem);
                        libraryAssets.getFilePopupMenu()
                                .showMenu(appUi, Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
                        eventBus.post(new AssetSelectedEvent(asset));
                    } else if (event.getButton() == Input.Buttons.LEFT) {
                        libraryAssets.setSelected(assetItem);
                        eventBus.post(new AssetSelectedEvent(asset));
                    }
                }
            });
        }
    }

    public void initProjectAssets(@NotNull AssetLibraryViewer projectAssets) {

    }

    public void initAssetDock(@NotNull AssetsDock assetsDock) {
        eventBus.register(assetsDock);
    }

    public void iniLogBar(@NotNull LogBar logBar) {
        eventBus.register(logBar);
    }
}
