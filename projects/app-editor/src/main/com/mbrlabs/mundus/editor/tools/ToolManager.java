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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableObject;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.assets.EditorModelService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import com.mbrlabs.mundus.editor.core.shader.ShaderConstants;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.input.InputService;
import com.mbrlabs.mundus.editor.tools.brushes.CircleBrush;
import com.mbrlabs.mundus.editor.tools.brushes.ConfettiBrush;
import com.mbrlabs.mundus.editor.tools.brushes.SmoothCircleBrush;
import com.mbrlabs.mundus.editor.tools.brushes.StarBrush;
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.ui.AppUi;
import lombok.Getter;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 25-12-2015
 */
@Component
public class ToolManager extends InputAdapter implements Disposable, RenderableObject {

    private final EditorCtx ctx;
    private Tool activeTool;

    @Getter
    private final List<TerrainBrush> terrainBrushes = new ArrayList<>();
    public ModelPlacementTool modelPlacementTool;
    public SelectionTool selectionTool;
    public TranslateTool translateTool;
    public RotateTool rotateTool;
    public ScaleTool scaleTool;

    public ToolManager(EditorCtx ctx, AppUi appUi, EventBus eventBus,
                       EntityPicker picker, ToolHandlePicker toolHandlePicker, ShapeRenderer shapeRenderer,
                       EditorModelService modelService, CommandHistory history, SceneStorage sceneStorage,
                       EditorAssetManager editorAssetManager) {
        this.ctx = ctx;

        terrainBrushes.add(new SmoothCircleBrush(ctx, ShaderConstants.TERRAIN, history));
        terrainBrushes.add(new CircleBrush(ctx, ShaderConstants.TERRAIN, history));
        terrainBrushes.add(new StarBrush(ctx, ShaderConstants.TERRAIN, history));
        terrainBrushes.add(new ConfettiBrush(ctx, ShaderConstants.TERRAIN, history));

        modelPlacementTool =
                new ModelPlacementTool(ctx, ShaderConstants.MODEL, history, sceneStorage, modelService, appUi, eventBus,
                        editorAssetManager);
        selectionTool = new SelectionTool(ctx, ShaderConstants.WIREFRAME, picker, history, eventBus);
        translateTool = new TranslateTool(ctx, ShaderConstants.WIREFRAME, picker, toolHandlePicker, history,
                eventBus);
        rotateTool = new RotateTool(ctx, ShaderConstants.WIREFRAME, picker, toolHandlePicker, shapeRenderer, history,
                eventBus);
        scaleTool = new ScaleTool(ctx, ShaderConstants.WIREFRAME, picker, toolHandlePicker,
                shapeRenderer, history, appUi, eventBus);
    }

    public void activateTool(Tool tool) {
        boolean shouldKeepSelection =
                activeTool != null && activeTool instanceof SelectionTool && tool instanceof SelectionTool;
        int selected = getSelectedEntity();

        deactivateTool();
        activeTool = tool;
        activeTool.onActivated();

        if (shouldKeepSelection) {
            ((SelectionTool) activeTool).entitySelected(selected);
        }
    }

    public void deactivateTool() {
        if (activeTool == null) {
            return;
        }

        activeTool.onDisabled();
        activeTool = null;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        if (activeTool != null) {
            activeTool.render(batch, environment, shaders, delta);
        }
    }

    public void act() {
        if (activeTool != null) {
            activeTool.act();
        }
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

    private int getSelectedEntity() {
        if (activeTool == null) {
            return -1;
        }
        var scene = ctx.getCurrent().getCurrentScene();

        if (scene == null) {
            return -1;
        }
        return ctx.getSelectedEntityId();
    }

    public Tool getActiveTool() {
        return activeTool;
    }
}
