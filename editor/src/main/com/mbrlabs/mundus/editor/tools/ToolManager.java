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
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.EditorScene;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.input.InputManager;
import com.mbrlabs.mundus.editor.tools.brushes.*;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.ui.AppUi;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
@Component
public class ToolManager extends InputAdapter implements Disposable {

    private static final int KEY_DEACTIVATE = Input.Keys.ESCAPE;

    private final EditorCtx ctx;

    private Tool activeTool;

    public List<TerrainBrush> terrainBrushes;
    public ModelPlacementTool modelPlacementTool;
    public SelectionTool selectionTool;
    public TranslateTool translateTool;
    public RotateTool rotateTool;
    public ScaleTool scaleTool;

    private final InputManager inputManager;

    public ToolManager(EditorCtx ctx, AppUi appUi, EventBus eventBus, InputManager inputManager,
                       EditorAssetManager assetManager, GameObjectPicker goPicker,
                       ToolHandlePicker toolHandlePicker, ModelBatch modelBatch, ShapeRenderer shapeRenderer,
                       CommandHistory history) {
        this.ctx = ctx;
        this.inputManager = inputManager;
        this.activeTool = null;

        terrainBrushes = new ArrayList<>();
        terrainBrushes.add(new SmoothCircleBrush(ctx, assetManager, modelBatch, history));
        terrainBrushes.add(new CircleBrush(ctx, assetManager, modelBatch, history));
        terrainBrushes.add(new StarBrush(ctx, assetManager, modelBatch, history));
        terrainBrushes.add(new ConfettiBrush(ctx, assetManager, modelBatch, history));

        modelPlacementTool = new ModelPlacementTool(ctx, modelBatch, history, appUi, eventBus);
        selectionTool = new SelectionTool(ctx, goPicker, modelBatch, history, eventBus);
        translateTool = new TranslateTool(ctx, goPicker, toolHandlePicker, modelBatch, history, eventBus);
        rotateTool = new RotateTool(ctx, goPicker, toolHandlePicker, shapeRenderer, modelBatch, history, eventBus);
        scaleTool = new ScaleTool(ctx, goPicker, toolHandlePicker, shapeRenderer, modelBatch, history, appUi, eventBus);
    }

    public void activateTool(Tool tool) {
        boolean shouldKeepSelection = activeTool != null && activeTool instanceof SelectionTool && tool instanceof SelectionTool;
        GameObject selected = getSelectedObject();

        deactivateTool();
        activeTool = tool;
        inputManager.addProcessor(activeTool);
        activeTool.onActivated();

        if (shouldKeepSelection) {
            ((SelectionTool) activeTool).gameObjectSelected(selected);
        }
    }

    public void deactivateTool() {
        if (activeTool != null) {
            activeTool.onDisabled();
            inputManager.removeProcessor(activeTool);
            activeTool = null;
        }
    }

    public void setDefaultTool() {
        if (activeTool == null || activeTool == modelPlacementTool || activeTool instanceof TerrainBrush)
            activateTool(translateTool);
        else
            activeTool.onDisabled();

    }

    public void render() {
        if (activeTool != null) {
            activeTool.render();
        }
    }

    public void act() {
        if (activeTool != null) {
            activeTool.act();
        }
    }

    public Tool getActiveTool() {
        return activeTool;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == KEY_DEACTIVATE) {
            if (activeTool != null) {
                activeTool.onDisabled();
            }
            setDefaultTool();
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        for (TerrainBrush brush : terrainBrushes) {
            brush.dispose();
        }
        translateTool.dispose();
        modelPlacementTool.dispose();
        selectionTool.dispose();
        rotateTool.dispose();
        scaleTool.dispose();
    }

    private GameObject getSelectedObject() {
        if (activeTool == null) {
            return null;
        }
        EditorScene scene = ctx.getCurrent().getCurrentScene();

        if (scene == null) {
            return null;
        }
        return ctx.getSelected();
    }

}
