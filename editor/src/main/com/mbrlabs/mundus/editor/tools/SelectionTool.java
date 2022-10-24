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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.tools.picker.GameObjectPicker;
import com.mbrlabs.mundus.editor.utils.Fa;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class SelectionTool extends Tool {

    public static final String NAME = "Selection Tool";

    private final GameObjectPicker goPicker;
    protected final EventBus eventBus;

    public SelectionTool(EditorCtx ctx, String shaderKey, GameObjectPicker goPicker, ModelBatch batch,
                         CommandHistory history, EventBus eventBus, String name) {
        super(ctx, shaderKey, batch, history, name);
        this.goPicker = goPicker;
        this.eventBus = eventBus;
    }

    public SelectionTool(EditorCtx ctx, String shaderKey, GameObjectPicker goPicker, ModelBatch batch,
                         CommandHistory history, EventBus eventBus) {
        this(ctx, shaderKey, goPicker, batch, history, eventBus, NAME);
    }

    public void gameObjectSelected(GameObject selection) {
        getCtx().setSelected(selection);
    }

    @Override
    public Drawable getIcon() {
        return null;
    }

    @Override
    public String getIconFont() {
        return Fa.Companion.getMOUSE_POINTER();
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        if (getCtx().getSelected() != null) {
            getBatch().begin(getCtx().getCamera());
            for (GameObject go : getCtx().getSelected()) {
                // model component
                ModelComponent mc = (ModelComponent) go.findComponentByType(Component.Type.MODEL);
                if (mc != null) {
                    getBatch().render(mc.getModelInstance(), shaders.get(getShaderKey()));
                }

                // terrainAsset component
                TerrainComponent tc = (TerrainComponent) go.findComponentByType(Component.Type.TERRAIN);
                if (tc != null) {
                    getBatch().render(tc.getTerrain().getTerrain(), shaders.get(getShaderKey()));
                }
            }
            getBatch().end();
        }
    }

    @Override
    public void act() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT) {
            GameObject selection = goPicker.pick(getCtx().getCurrent().getCurrentScene(), screenX, screenY);
            if (selection != null && !selection.equals(getCtx().getSelected())) {
                gameObjectSelected(selection);
                eventBus.post(new GameObjectSelectedEvent(selection));
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
        getCtx().setSelected(null);
    }

}
