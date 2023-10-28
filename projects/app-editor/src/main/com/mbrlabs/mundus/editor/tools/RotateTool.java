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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.RotateCommand;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import static net.nevinsky.abyssus.core.shader.ShaderProvider.DEFAULT_SHADER_KEY;

/**
 * Rotate tool for game objects
 *
 * @author Marcus Brummer, codengima
 * @version 30-09-2016
 */
public class RotateTool extends TransformTool {

    public static final String NAME = "Rotate Tool";

    private final RotateHandle xHandle;
    private final RotateHandle yHandle;
    private final RotateHandle zHandle;

    private final Matrix4 shapeRenderMat = new Matrix4();

    private final Quaternion tempQuat = new Quaternion();

    private final ShapeRenderer shapeRenderer;
    private RotateCommand command;
    private float lastRot = 0;
//    private final BitmapFont font;
//    private final SpriteBatch batch;

    public RotateTool(EditorCtx ctx, String shaderKey, EntityPicker picker, ToolHandlePicker handlePicker,
                      ShapeRenderer shapeRenderer, CommandHistory history, EventBus eventBus) {
        super(ctx, shaderKey, picker, handlePicker, history, eventBus);
        this.shapeRenderer = shapeRenderer;
        xHandle = new RotateHandle(X_HANDLE_ID, TransformState.TRANSFORM_X, COLOR_X);
        yHandle = new RotateHandle(Y_HANDLE_ID, TransformState.TRANSFORM_Y, COLOR_Y);
        zHandle = new RotateHandle(Z_HANDLE_ID, TransformState.TRANSFORM_Z, COLOR_Z);
        handles.add(xHandle);
        handles.add(yHandle);
        handles.add(zHandle);

        //todo move generating fonts to bean
//        var generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/robotoRegular.ttf"));
//        var params = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        params.kerning = true;
//        params.borderStraight = false;
//        params.genMipMaps = true;
//        params.hinting = FreeTypeFontGenerator.Hinting.Full;
//        params.size = 20;
//        font = generator.generateFont(params);
//        generator.dispose();
//        batch = new SpriteBatch();
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
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
                renderTool(batch.getCamera(), pivot, COLOR_X);
                return;
            case TRANSFORM_Y:
                renderTool(batch.getCamera(), pivot, COLOR_Y);
                return;
            case TRANSFORM_Z:
                renderTool(batch.getCamera(), pivot, COLOR_Z);
        }
    }

    private void renderTool(Camera camera, Vector3 pivot, Color color) {
//        batch.setProjectionMatrix(shapeRenderMat);
//        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.setProjectionMatrix(shapeRenderMat);

        var mouseX = Gdx.input.getX();
        var mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        shapeRenderer.rectLine(pivot.x, pivot.y, mouseX, mouseY, 2);
        shapeRenderer.rectLine(mouseX, mouseY, mouseX + 10, mouseY, 2);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(pivot.x, pivot.y, 7);
        shapeRenderer.end();

//        font.setColor(Color.BLACK);
//        font.draw(batch, String.format("%.2fº", lastRot), mouseX, mouseY);

//        batch.end();
    }

    @Override
    public void act() {
        super.act();

        if (getCtx().getSelectedEntityId() < 0) {
            return;
        }
        scaleHandles();
        translateHandles();
        if (state == TransformState.IDLE) {
            return;
        }

        float angle = getCurrentAngle();
        float rot = angle - lastRot;

        var positionComponent = getCtx().getSelectedEntity().getComponent(PositionComponent.class);
        //todo do not create temp quaternion
        var tempQuat = new Quaternion();
        boolean modified = true;
        if (state == TransformState.TRANSFORM_X) {
            tempQuat.setEulerAngles(0, -rot, 0);
        } else if (state == TransformState.TRANSFORM_Y) {
            tempQuat.setEulerAngles(-rot, 0, 0);
        } else if (state == TransformState.TRANSFORM_Z) {
            tempQuat.setEulerAngles(0, 0, -rot);
        } else {
            modified = false;
        }

        positionComponent.getLocalRotation().mul(tempQuat);

        if (modified) {
            entityModifiedEvent.setEntityId(getCtx().getSelectedEntityId());
            eventBus.post(entityModifiedEvent);
        }

        lastRot = angle;
    }

    private float getCurrentAngle() {
        if (getCtx().getSelectedEntityId() < 0) {
            return 0;
        }

        var positionComponent = getCtx().getSelectedEntity().getComponent(PositionComponent.class);
        positionComponent.getPosition(temp0);
        // fill pivot vector
        getCtx().getCurrent().getCamera().project(temp0);
        //fill mouse vector
        temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);

        return MathUtils.angle(temp0.x, temp0.y, temp1.x, temp1.y);
    }

    @Override
    protected void scaleHandles() {
        if (getCtx().getSelectedEntityId() < 0 ||
                getCtx().getSelectedEntity().getComponent(PositionComponent.class) == null) {
            return;
        }

        Vector3 pos = getCtx().getSelectedEntity().getComponent(PositionComponent.class).getPosition(temp0);
        var scaleFactor = getCtx().getCurrent().getCamera().position.dst(pos) * 0.005f;

        handles.forEach(handle -> {
            handle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
            handle.applyTransform();
        });
    }

    @Override
    protected void rotateHandles() {
        xHandle.getRotationEuler().set(0, 90, 0);
        yHandle.getRotationEuler().set(90, 0, 0);
        zHandle.getRotationEuler().set(0, 0, 0);
        handles.forEach(ToolHandle::applyTransform);
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
        RotateHandle handle = (RotateHandle) handlePicker.pick(handles, screenX, screenY);
        if (handle == null) {
            state = TransformState.IDLE;
            return false;
        }

        state = handle.getState();
        handle.changeColor(COLOR_SELECTED);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (state != TransformState.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);

//         command.setAfter(getCtx().getSelectedEntityId().getLocalRotation(tempQuat));
//            getHistory().add(command);
            command = null;
            state = TransformState.IDLE;
        }
        return false;
    }


    @Override
    @NotNull
    public SymbolIcon getIcon() {
        return SymbolIcon.REFRESH;
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
            if (id == X_HANDLE_ID) {
                getRotationEuler().y = 90;
                getScale().x = 0.9f;
                getScale().y = 0.9f;
                getScale().z = 0.9f;
            } else if (id == Y_HANDLE_ID) {
                getRotationEuler().x = 90;
            } else if (id == Z_HANDLE_ID) {
                getRotationEuler().z = 90;
                getScale().x = 1.1f;
                getScale().y = 1.1f;
                getScale().z = 1.1f;
            }
            modelInstance.transform.translate(0, 100, 0);
        }

        @Override
        public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
            batch.render(modelInstance, DEFAULT_SHADER_KEY);
        }

        @Override
        public void renderPick(ModelBatch modelBatch, ShaderProvider shaders) {
            modelBatch.render(modelInstance, DEFAULT_SHADER_KEY);
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
