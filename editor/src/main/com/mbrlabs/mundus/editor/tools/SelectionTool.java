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
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import org.jetbrains.annotations.NotNull;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class SelectionTool extends Tool {

    public static final String NAME = "Selection Tool";
    private final EntityPicker picker;
    protected final EventBus eventBus;

    public SelectionTool(EditorCtx ctx, String shaderKey, EntityPicker picker,
                         CommandHistory history, EventBus eventBus, String name) {
        super(ctx, shaderKey, history, name);
        this.picker = picker;
        this.eventBus = eventBus;
    }

    public SelectionTool(EditorCtx ctx, String shaderKey, EntityPicker picker, CommandHistory history,
                         EventBus eventBus) {
        this(ctx, shaderKey, picker, history, eventBus, NAME);
    }

    public void entitySelected(int entityId) {
        getCtx().selectEntity(entityId);
    }

    @Override
    @NotNull
    public SymbolIcon getIcon() {
        return SymbolIcon.POINTER;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        if (getCtx().getSelectedEntity() == null) {
            return;
        }
        batch.begin(getCtx().getCurrent().getCamera());

        //todo
//        for (GameObject go : getCtx().getSelectedEntityId()) {
//            // model component
//            ModelComponent mc = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
//            if (mc != null) {
//                batch.render(mc.getModelInstance(), shaders.get(getShaderKey()));
//            }
//
//            // terrainAsset component
//            TerrainComponent tc = (TerrainComponent) go.findComponentByType(Component.Type.TERRAIN);
//            if (tc != null) {
//                batch.render(tc.getTerrain().getTerrain(), shaders.get(getShaderKey()));
//            }
//        }
        batch.end();
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            int entityId = picker.pick(getCtx().getCurrent().getCurrentScene(), screenX, screenY);
            if (entityId >= 0 && entityId != getCtx().getSelectedEntityId()) {
                entitySelected(entityId);
                eventBus.post(new EntitySelectedEvent(entityId));
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //todo
//        projectManager.current.getCurrentScene().viewport.getScreenHeight();

        return false;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onActivated() {
    }

    @Override
    public void onDisabled() {
        getCtx().selectEntity(-1);
    }

}
