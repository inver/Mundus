package com.mbrlabs.mundus.editor.ui.modules.dock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.AssetImportEvent;
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import com.mbrlabs.mundus.editor.ui.modules.dock.assets.AssetItem;
import com.mbrlabs.mundus.editor.ui.modules.dock.assets.AssetsDock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DockBarPresenter {

    private final EditorCtx ctx;
    private final EditorAssetManager assetManager;
    private final EventBus eventBus;
    private final AppUi appUi;
    private final PreviewGenerator previewGenerator;

    public void initAssetDock(@NotNull AssetsDock assetsDock) {
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> reloadAssets(assetsDock));
        eventBus.register((AssetImportEvent.AssetImportListener) event -> reloadAssets(assetsDock));
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> assetsDock.setSelected(null));

        assetsDock.getAddAssetToScene().addListener(createAddAssetToSceneListener(assetsDock));
        assetsDock.getRenameAsset().addListener(createDeleteAssetListener(assetsDock));
        assetsDock.getDeleteAsset().addListener(createDeleteAssetListener(assetsDock));

        reloadAssets(assetsDock);
    }

    private void reloadAssets(AssetsDock assetsDock) {
        assetsDock.getAssetsView().clearChildren();

        for (var asset : ctx.getAssetLibrary().values()) {
            var assetItem = new AssetItem(asset, previewGenerator);
            assetsDock.getAssetsView().addActor(assetItem);
            assetItem.addListener(new InputListener() {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    log.debug("touch down {},{}", x, y);
                    if (event.getButton() == Input.Buttons.RIGHT) {
                        assetsDock.setSelected(assetItem);
                        assetsDock.getPopupMenu()
                                .showMenu(appUi, Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
                        eventBus.post(new AssetSelectedEvent(asset));
                    } else if (event.getButton() == Input.Buttons.LEFT) {
                        assetsDock.setSelected(assetItem);
                        eventBus.post(new AssetSelectedEvent(asset));
                    }
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }
            });
        }
    }

    private ClickListener createDeleteAssetListener(AssetsDock assetsDock) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (assetsDock.getSelected() != null && assetsDock.getSelected().getAsset() != null) {
                    assetManager.deleteAsset(assetsDock.getSelected().getAsset());
                }
            }
        };
    }

    private ClickListener createAddAssetToSceneListener(AssetsDock assetsDock) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (assetsDock.getSelected() == null) {
                    return;
                }

                var asset = assetsDock.getSelected().getAsset();

                try {
                    assetManager.copyAssetToProjectFolder(asset);
                    var projectAsset = assetManager.loadCurrentProjectAsset(asset.getName());
                    ctx.getCurrent().getProjectAssets()
                            .put(new AssetKey(projectAsset.getType(), projectAsset.getName()), projectAsset);
                    eventBus.post(new AssetImportEvent(asset));
                    eventBus.post(new SceneGraphChangedEvent());
                } catch (Exception e) {
                    log.error("ERROR", e);
                }
            }
        };
    }


    public void iniLogBar(@NotNull LogBar logBar) {
        eventBus.register(logBar);
    }
}
