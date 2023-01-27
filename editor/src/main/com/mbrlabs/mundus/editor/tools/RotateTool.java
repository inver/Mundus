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
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.RotateCommand;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.utils.Fa;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * Rotate tool for game objects
 *
 * @author Marcus Brummer, codengima
 * @version 30-09-2016
 */
public class RotateTool extends TransformTool {

    public static final String NAME = "Rotate Tool";

    private RotateHandle xHandle;
    private RotateHandle yHandle;
    private RotateHandle zHandle;
    private RotateHandle[] handles;

    private Matrix4 shapeRenderMat = new Matrix4();

    private Vector3 temp0 = new Vector3();
    private Vector3 temp1 = new Vector3();
    private Quaternion tempQuat = new Quaternion();

    private ShapeRenderer shapeRenderer;

    private TransformState state = TransformState.IDLE;
    private RotateCommand currentRotateCommand;
    private float lastRot = 0;

    public RotateTool(EditorCtx ctx, String shaderKey, EntityPicker picker, ToolHandlePicker handlePicker,
                      ShapeRenderer shapeRenderer, ModelBatch batch, CommandHistory history, EventBus eventBus) {
        super(ctx, shaderKey, picker, handlePicker, batch, history, eventBus, NAME);
        this.shapeRenderer = shapeRenderer;
        xHandle = new RotateHandle(X_HANDLE_ID, TransformState.TRANSFORM_X, COLOR_X);
        yHandle = new RotateHandle(Y_HANDLE_ID, TransformState.TRANSFORM_Y, COLOR_Y);
        zHandle = new RotateHandle(Z_HANDLE_ID, TransformState.TRANSFORM_Z, COLOR_Z);
        handles = new RotateHandle[]{xHandle, yHandle, zHandle};
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        super.render(batch, environment, shaders, delta);
        if (getCtx().getSelectedEntityId() < 0) {
            return;
        }

        if (state == TransformState.IDLE) {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            batch.begin(getCtx().getCurrent().getCamera());
            xHandle.render(batch, environment, shaders, delta);
            yHandle.render(batch, environment, shaders, delta);
            zHandle.render(batch, environment, shaders, delta);
            batch.end();
            return;
        }

        var vp = getCtx().getViewport();
        getCtx().getSelectedEntity().getComponent(PositionComponent.class).getLocalPosition(temp0);
        var pivot = getCtx().getCurrent().getCamera().project(temp0);

        shapeRenderMat.setToOrtho2D(vp.getScreenX(), vp.getScreenY(), vp.getScreenWidth(), vp.getScreenHeight());
        switch (state) {
            case TRANSFORM_X:
                renderTool(pivot, COLOR_X);
                return;
            case TRANSFORM_Y:
                renderTool(pivot, COLOR_Y);
                return;
            case TRANSFORM_Z:
                renderTool(pivot, COLOR_Z);
        }
    }

    private void renderTool(Vector3 pivot, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.setProjectionMatrix(shapeRenderMat);
        shapeRenderer.rectLine(pivot.x, pivot.y, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 2);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(pivot.x, pivot.y, 7);
        shapeRenderer.end();
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

        float angle = getCurrentAngle();
        float rot = angle - lastRot;

        boolean modified = false;
//        if (null != state) {
//            switch (state) {
//                case TRANSFORM_X:
//                    tempQuat.setEulerAngles(0, -rot, 0);
//                    getCtx().getSelectedEntityId().rotate(tempQuat);
//                    modified = true;
//                    break;
//                case TRANSFORM_Y:
//                    tempQuat.setEulerAngles(-rot, 0, 0);
//                    getCtx().getSelectedEntityId().rotate(tempQuat);
//                    modified = true;
//                    break;
//                case TRANSFORM_Z:
//                    tempQuat.setEulerAngles(0, 0, -rot);
//                    getCtx().getSelectedEntityId().rotate(tempQuat);
//                    modified = true;
//                    break;
//                default:
//                    break;
//            }
//        }

//        if (modified) {
//            entityModifiedEvent.setGameObject(getCtx().getSelectedEntityId());
//            eventBus.post(entityModifiedEvent);
//        }

        lastRot = angle;
    }

    private float getCurrentAngle() {
        if (getCtx().getSelectedEntityId() < 0) {
            return 0;
        }

//        getCtx().getSelectedEntityId().getPosition(temp0);
//        Vector3 pivot = getCtx().getCamera().project(temp0);
//        Vector3 mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
//
//        return MathUtils.angle(pivot.x, pivot.y, mouse.x, mouse.y);

        return 0;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (button != Input.Buttons.LEFT || getCtx().getSelectedEntityId() < 0) {
            return false;
        }


        lastRot = getCurrentAngle();

//        currentRotateCommand = new RotateCommand(getCtx().getSelectedEntityId());
//        currentRotateCommand.setBefore(getCtx().getSelectedEntityId().getLocalRotation(tempQuat));
//
//        RotateHandle handle = (RotateHandle) handlePicker.pick(handles, screenX, screenY);
//        if (handle == null) {
//            state = TransformState.IDLE;
//            return false;
//        }

//        switch (handle.getId()) {
//            case X_HANDLE_ID:
//                state = TransformState.TRANSFORM_X;
//                break;
//            case Y_HANDLE_ID:
//                state = TransformState.TRANSFORM_Y;
//                break;
//            case Z_HANDLE_ID:
//                state = TransformState.TRANSFORM_Z;
//                break;
//            default:
//                break;
//        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        state = TransformState.IDLE;
        if (currentRotateCommand != null) {
//            currentRotateCommand.setAfter(getCtx().getSelectedEntityId().getLocalRotation(tempQuat));
//            getHistory().add(currentRotateCommand);
//            currentRotateCommand = null;
        }
        return false;
    }

    @Override
    public void entitySelected(int entityId) {
        super.entitySelected(entityId);
        scaleHandles();
        rotateHandles();
        translateHandles();
    }

    @Override
    protected void rotateHandles() {
        xHandle.getRotationEuler().set(0, 90, 0);
        xHandle.applyTransform();
        yHandle.getRotationEuler().set(90, 0, 0);
        yHandle.applyTransform();
        zHandle.getRotationEuler().set(0, 0, 0);
        zHandle.applyTransform();
    }

    @Override
    protected void translateHandles() {
        if (getCtx().getSelectedEntityId() < 0) {
            return;
        }

        final Vector3 pos = getCtx().getSelectedEntity().getComponent(PositionComponent.class).getPosition(temp0);
        xHandle.getPosition().set(pos);
        xHandle.applyTransform();
        yHandle.getPosition().set(pos);
        yHandle.applyTransform();
        zHandle.getPosition().set(pos);
        zHandle.applyTransform();
    }

    @Override
    protected void scaleHandles() {
//        if (getCtx().getSelectedEntityId() == null) {
//            return;
//        }
//
//        var pos = getCtx().getSelectedEntityId().getPosition(temp0);
//        var scaleFactor = getCtx().getCamera().position.dst(pos) * 0.005f;
//        xHandle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
//        xHandle.applyTransform();
//
//        yHandle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
//        yHandle.applyTransform();
//
//        zHandle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
//        zHandle.applyTransform();
    }

    @NotNull
    @Override
    public String getIconFont() {
        return Fa.Companion.getREFRESH();
    }

    @Override
    public void dispose() {
        super.dispose();
        xHandle.dispose();
        yHandle.dispose();
        zHandle.dispose();
    }

    /**
     *
     */
    private class RotateHandle extends ToolHandle {

        public RotateHandle(int id, TransformState state, Color color) {
            super(id, state, UsefulMeshs.torus(
                    new Material(ColorAttribute.createDiffuse(color)), 20, 1f, 50, 50
            ));
            modelInstance.materials.first().set(idAttribute);
//            switch (id) {
//                case X_HANDLE_ID:
//                    this.getRotationEuler().y = 90;
//                    this.getScale().x = 0.9f;
//                    this.getScale().y = 0.9f;
//                    this.getScale().z = 0.9f;
//                    break;
//                case Y_HANDLE_ID:
//                    this.getRotationEuler().x = 90;
//                    break;
//                case Z_HANDLE_ID:
//                    this.getRotationEuler().z = 90;
//                    this.getScale().x = 1.1f;
//                    this.getScale().y = 1.1f;
//                    this.getScale().z = 1.1f;
//                    break;
//            }
            modelInstance.transform.translate(0, 100, 0);
        }


        @Override
        public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
            batch.render(modelInstance);
        }

        @Override
        public void renderPick(ModelBatch modelBatch, ShaderHolder shaders) {
            getBatch().render(modelInstance, shaders.get(getShaderKey()));
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
            model.dispose();
        }
    }

}
