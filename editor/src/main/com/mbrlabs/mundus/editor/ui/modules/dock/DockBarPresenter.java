package com.mbrlabs.mundus.editor.ui.modules.dock;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.*;
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
    private final ProjectManager projectManager;
    private final EventBus eventBus;
    private final AppUi appUi;
    private final PreviewGenerator previewGenerator;

    public void initAssetDock(@NotNull AssetsDock assetsDock) {
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> reloadAssets(assetsDock));
        eventBus.register((AssetImportEvent.AssetImportListener) event -> reloadAssets(assetsDock));
        eventBus.register((GameObjectSelectedEvent.GameObjectSelectedListener) event -> assetsDock.setSelected(null));

        assetsDock.getAddAssetToScene().addListener(createAddAssetToSceneListener(assetsDock));
        assetsDock.getRenameAsset().addListener(createDeleteAssetListener(assetsDock));
        assetsDock.getDeleteAsset().addListener(createDeleteAssetListener(assetsDock));

        reloadAssets(assetsDock);
    }

    private void reloadAssets(AssetsDock assetsDock) {
        assetsDock.getAssetsView().clearChildren();

        for (var asset : assetManager.getAssets()) {
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
                    var context = ctx.getCurrent();
                    var sceneGraph = context.getCurrentScene().getSceneGraph();
                    var goID = context.obtainID();
                    var name = asset.getType() + "_" + goID;

                    // create asset
//                        var asset = context.getAssetManager().createModelAsset(it.file)

//                        asset.load()
//                        asset.applyDependencies()
                    GameObject go = assetManager.convert(goID, name, asset);

//                    var modelGO = GameObjectUtils.createModelGO(
//                            sceneGraph, Shaders.modelShader, goID, name,
//                            it as ModelAsset ?
//                    )
//                        var terrainGO = createTerrainGO(
//                            sceneGraph,
//                            Shaders.terrainShader, goID, name, asset
//                        )
                    // update sceneGraph
                    sceneGraph.addGameObject(go);

                    // update outline
                    //todo
//                        addGoToTree(null, terrainGO)

//                        context.getCurrentScene()..add(asset)
                    projectManager.saveProject(context);
                    eventBus.post(new AssetImportEvent(asset));
                    eventBus.post(new SceneGraphChangedEvent());
//                    Mundus.postEvent(AssetImportEvent(it))
//                    Mundus.postEvent(SceneGraphChangedEvent())
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
