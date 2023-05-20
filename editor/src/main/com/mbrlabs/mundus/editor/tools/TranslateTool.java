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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntityModifiedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.TranslateCommand;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
public class TranslateTool extends TransformTool {

    private final float ARROW_THIKNESS = 0.1f;
    private final float ARROW_CAP_SIZE = 0.15f;
    private final int ARROW_DIVISIONS = 12;

    public static final String NAME = "Translate Tool";

    private TransformState state = TransformState.IDLE;
    private boolean initTranslate = true;

    private final TranslateHandle xHandle;
    private final TranslateHandle yHandle;
    private final TranslateHandle zHandle;
    private final TranslateHandle xzPlaneHandle;
    private final TranslateHandle[] handles;

    private final Vector3 lastPos = new Vector3();
    private boolean globalSpace = true;

    private final Vector3 temp0 = new Vector3();

    private TranslateCommand command;

    public TranslateTool(EditorCtx ctx, String shaderKey, EntityPicker picker, ToolHandlePicker handlePicker,
                         CommandHistory history, EventBus eventBus) {

        super(ctx, shaderKey, picker, handlePicker, history, eventBus, NAME);

        ModelBuilder modelBuilder = new ModelBuilder();

        Model xHandleModel = modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE,
                ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_X)),
                VertexAttributes.Usage.Position);
        Model yHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE,
                ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                VertexAttributes.Usage.Position);
        Model zHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE,
                ARROW_THIKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                VertexAttributes.Usage.Position);
        Model xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
                new Material(ColorAttribute.createDiffuse(COLOR_XZ)), VertexAttributes.Usage.Position);

        xHandle = new TranslateHandle(COLOR_X.toIntBits(), TransformState.TRANSFORM_X, xHandleModel);
        yHandle = new TranslateHandle(COLOR_Y.toIntBits(), TransformState.TRANSFORM_Y, yHandleModel);
        zHandle = new TranslateHandle(COLOR_Z.toIntBits(), TransformState.TRANSFORM_Z, zHandleModel);
        xzPlaneHandle = new TranslateHandle(COLOR_XZ.toIntBits(), TransformState.TRANSFORM_XZ, xzPlaneHandleModel);
        handles = new TranslateHandle[]{xHandle, yHandle, zHandle, xzPlaneHandle};

        entityModifiedEvent = new EntityModifiedEvent(-1);
    }

    @Override
    @NotNull
    public SymbolIcon getIcon() {
        return SymbolIcon.TRANSLATE;
    }

    @Override
    public void entitySelected(int entityId) {
        super.entitySelected(entityId);
        scaleHandles();
        translateHandles();
    }

    public void setGlobalSpace(boolean global) {
        this.globalSpace = global;
        xHandle.getRotation().idt();
        xHandle.applyTransform();

        yHandle.getRotation().idt();
        yHandle.applyTransform();

        zHandle.getRotation().idt();
        zHandle.applyTransform();
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        super.render(batch, environment, shaders, delta);
        if (getCtx().getSelectedEntityId() < 0) {
            return;
        }
        batch.begin(getCtx().getCurrent().getCamera());
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        xHandle.render(batch, environment, shaders, delta);
        yHandle.render(batch, environment, shaders, delta);
        zHandle.render(batch, environment, shaders, delta);
        xzPlaneHandle.render(batch, environment, shaders, delta);

        batch.end();
    }

    @Override
    public void act() {
        super.act();

        if (getCtx().getSelectedEntityId() < 0) {
            return;
        }
        translateHandles();
        if (state == TransformState.IDLE) {
            return;
        }

        Ray ray = getCtx().getViewport().getPickRay(Gdx.input.getX(), Gdx.input.getY());

        var positionComponent = getCtx().getSelectedEntity().getComponent(PositionComponent.class);
        Vector3 rayEnd = positionComponent.getLocalPosition(temp0);
        float dst = getCtx().getCurrent().getCamera().position.dst(rayEnd);
        rayEnd = ray.getEndPoint(rayEnd, dst);

        if (initTranslate) {
            initTranslate = false;
            lastPos.set(rayEnd);
        }

//        GameObject go = getCtx().getSelectedEntityId();

        boolean modified = false;
        Vector3 vec = new Vector3();
        if (state == TransformState.TRANSFORM_XZ) {
            vec.set(rayEnd.x - lastPos.x, 0, rayEnd.z - lastPos.z);
            modified = true;
        } else if (state == TransformState.TRANSFORM_X) {
            vec.set(rayEnd.x - lastPos.x, 0, 0);
            modified = true;
        } else if (state == TransformState.TRANSFORM_Y) {
            vec.set(0, rayEnd.y - lastPos.y, 0);
            modified = true;
        } else if (state == TransformState.TRANSFORM_Z) {
            vec.set(0, 0, rayEnd.z - lastPos.z);
            modified = true;
        }

        // TODO translation in global space
        // if(globalSpace) {
        // System.out.println("Before: " + vec);
        // System.out.println("After: " + vec);
        // }

        positionComponent.translate(vec);

        if (modified) {
            entityModifiedEvent.setEntityId(getCtx().getSelectedEntityId());
            eventBus.post(entityModifiedEvent);
        }

        lastPos.set(rayEnd);
    }

    @Override
    protected void scaleHandles() {
        if (getCtx().getSelectedEntityId() < 0) {
            return;
        }

        Vector3 pos = getCtx().getSelectedEntity().getComponent(PositionComponent.class).getPosition(temp0);
        float scaleFactor = getCtx().getCurrent().getCamera().position.dst(pos) * 0.25f;
        xHandle.getScale().set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        xHandle.applyTransform();

        yHandle.getScale().set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        yHandle.applyTransform();

        zHandle.getScale().set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        zHandle.applyTransform();

        xzPlaneHandle.getScale().set(scaleFactor * 0.13f, scaleFactor * 0.13f, scaleFactor * 0.13f);
        xzPlaneHandle.applyTransform();
    }

    @Override
    protected void translateHandles() {
        if (getCtx().getSelectedEntityId() < 0) {
            return;
        }


        final Vector3 pos =
                getCtx().getSelectedEntity().getComponent(PositionComponent.class).getTransform().getTranslation(temp0);
        xHandle.getPosition().set(pos);
        xHandle.applyTransform();
        yHandle.getPosition().set(pos);
        yHandle.applyTransform();
        zHandle.getPosition().set(pos);
        zHandle.applyTransform();
        xzPlaneHandle.getPosition().set(pos);
        xzPlaneHandle.applyTransform();
    }

    @Override
    protected void rotateHandles() {
        // no rotation for this one
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return state != TransformState.IDLE;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (button != Input.Buttons.LEFT || getCtx().getSelectedEntityId() < 0) {
            return false;
        }

        var handle = (TranslateHandle) handlePicker.pick(handles, screenX, screenY);
        if (handle == null) {
            state = TransformState.IDLE;
            return false;
        }

        state = handle.getState();
        initTranslate = true;
        handle.changeColor(COLOR_SELECTED);

//        command = new TranslateCommand(getCtx().getSelectedEntityId());
//        command.setBefore(getCtx().getSelectedEntityId().getLocalPosition(temp0));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (state != TransformState.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);
            xzPlaneHandle.changeColor(COLOR_XZ);

//            command.setAfter(getCtx().getSelectedEntityId().getLocalPosition(temp0));
//            getHistory().add(command);
            command = null;
            state = TransformState.IDLE;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
        xzPlaneHandle.dispose();
    }

    /**
     *
     */
    private class TranslateHandle extends ToolHandle {

        private final Model model;
        private final ModelInstance modelInstance;

        public TranslateHandle(int id, TransformState state, Model model) {
            super(id, state);
            this.model = model;
            this.modelInstance = new ModelInstance(model);
            modelInstance.materials.first().set(getIdAttribute());
        }

        public void changeColor(Color color) {
            ColorAttribute diffuse = (ColorAttribute) modelInstance.materials.get(0).get(ColorAttribute.Diffuse);
            diffuse.color.set(color);
        }

        @Override
        public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
            batch.render(modelInstance, shaders.get());
        }

        @Override
        public void renderPick(ModelBatch modelBatch, ShaderProvider shaders) {
            modelBatch.render(modelInstance, shaders.get(getShaderKey()));
        }

        @Override
        public void act() {

        }

        @Override
        public void applyTransform() {
            getRotation().setEulerAngles(getRotationEuler().y, getRotationEuler().x, getRotationEuler().z);
            modelInstance.transform.set(getPosition(), getRotation(), getScale());
        }

        @Override
        public void dispose() {
            this.model.dispose();
        }

    }

}
