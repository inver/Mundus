/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.assets.EditorModelService;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelInstance;
import org.jetbrains.annotations.NotNull;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
public class ModelPlacementTool extends Tool {

    public static final String NAME = "Placement Tool";
    public static Vector3 DEFAULT_ORIENTATION = Vector3.Z.cpy();

    private final Vector3 tempV3 = new Vector3();
    private boolean shouldRespectTerrainSlope = false;
    // DO NOT DISPOSE THIS
    private ModelAsset asset;
    private ModelInstance modelInstance;

    private final AppUi appUi;
    private final EventBus eventBus;
    private final EditorModelService modelService;
    private final SceneStorage sceneStorage;
    private final EditorAssetManager assetManager;

    public ModelPlacementTool(EditorCtx ctx, String shaderKey, CommandHistory history, SceneStorage sceneStorage,
                              EditorModelService modelService, AppUi appUi, EventBus eventBus,
                              EditorAssetManager assetManager) {
        super(ctx, shaderKey, history, NAME);
        this.appUi = appUi;
        this.eventBus = eventBus;
        this.modelService = modelService;
        this.sceneStorage = sceneStorage;
        this.assetManager = assetManager;
    }

    //todo rethink this method. May by move copy and load asset to another place?
    public void setModel(ModelAsset asset) {
        var currentProject = ctx.getCurrent();
        sceneStorage.copyAssetToProject(currentProject.getPath(), asset);

        var sceneAsset = assetManager.loadProjectAsset(currentProject.getPath(), asset.getName());
        currentProject.getProjectAssets().put(new AssetKey(AssetType.MODEL, sceneAsset.getName()), sceneAsset);
        this.asset = (ModelAsset) sceneAsset;

        var model = modelService.createFromAsset(this.asset);
        modelInstance = model.getModelInstance();
    }

    @Override
    @NotNull
    public SymbolIcon getIcon() {
        throw new UnsupportedOperationException();
    }

    public boolean isShouldRespectTerrainSlope() {
        return shouldRespectTerrainSlope;
    }

    public void setShouldRespectTerrainSlope(boolean shouldRespectTerrainSlope) {
        this.shouldRespectTerrainSlope = shouldRespectTerrainSlope;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, String shaderKey, float delta) {
        if (modelInstance != null) {
            batch.begin(ctx.getCurrent().getCamera());
            batch.render(modelInstance, environment, getShaderKey());
            batch.end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (modelInstance == null || button != Input.Buttons.LEFT) {
            return false;
        }


        var id = modelService.createModelEntity(asset);
        modelInstance.transform.getTranslation(tempV3);
        var positionComponent = ctx.getComponentByEntityId(id, PositionComponent.class);
        positionComponent.translate(tempV3);


        eventBus.post(new SceneGraphChangedEvent());
        mouseMoved(screenX, screenY);

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (this.asset == null || modelInstance == null) {
            return false;
        }

        final ProjectContext context = ctx.getCurrent();

        final Ray ray = ctx.getViewport().getPickRay(screenX, screenY);
        //todo add terrain processing
//        if (context.getCurrentScene().terrains.size > 0 && modelInstance != null) {
//            MeshPartBuilder.VertexInfo vi = TerrainUtils.getRayIntersectionAndUp
//                    (context.getCurrentScene().terrains, ray);
//            if (vi != null) {
//                if (shouldRespectTerrainSlope) {
//                    modelInstance.transform.setToLookAt(DEFAULT_ORIENTATION, vi.normal);
//                }
//                modelInstance.transform.setTranslation(vi.position);
//            }
//        } else {
        tempV3.set(ctx.getCurrent().getCamera().position);
        tempV3.add(ray.direction.nor().scl(200));
        modelInstance.transform.setTranslation(tempV3);
//        }

        return false;
    }

    @Override
    public void dispose() {
        this.asset = null;
        this.modelInstance = null;
    }

    @Override
    public void onActivated() {

    }

    @Override
    public void onDisabled() {
        dispose();
    }

}
