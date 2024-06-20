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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.TranslateCommand;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.model.Model;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * @author Marcus Brummer
 * @version 26-12-2015
 */
@Slf4j
public class TranslateTool extends TransformTool {
    public static final String NAME = "Translate Tool";

    private static final float ARROW_THICKNESS = 0.1f;
    private static final float ARROW_CAP_SIZE = 0.15f;
    private static final int ARROW_DIVISIONS = 12;

    private boolean initTranslate = true;
    private final TranslateHandle xHandle;
    private final TranslateHandle yHandle;
    private final TranslateHandle zHandle;
    private final TranslateHandle xzPlaneHandle;

    private final Vector3 lastPos = new Vector3();
    private boolean globalSpace = true;
    private TranslateCommand command;

    public TranslateTool(EditorCtx ctx, String shaderKey, EntityPicker picker, ToolHandlePicker handlePicker,
                         CommandHistory history, EventBus eventBus) {

        super(ctx, shaderKey, picker, handlePicker, history, eventBus);

        ModelBuilder modelBuilder = new ModelBuilder();

        Model xHandleModel = modelBuilder.createArrow(0, 0, 0, 1, 0, 0, ARROW_CAP_SIZE,
                ARROW_THICKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_X)),
                VertexAttributes.Usage.Position);
        Model yHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 1, 0, ARROW_CAP_SIZE,
                ARROW_THICKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                VertexAttributes.Usage.Position);
        Model zHandleModel = modelBuilder.createArrow(0, 0, 0, 0, 0, 1, ARROW_CAP_SIZE,
                ARROW_THICKNESS, ARROW_DIVISIONS,
                GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                VertexAttributes.Usage.Position);
        Model xzPlaneHandleModel = modelBuilder.createSphere(1, 1, 1, 20, 20,
                new Material(ColorAttribute.createDiffuse(COLOR_XZ)), VertexAttributes.Usage.Position);

        xHandle = new TranslateHandle(COLOR_X.toIntBits(), TransformState.TRANSFORM_X, xHandleModel);
        yHandle = new TranslateHandle(COLOR_Y.toIntBits(), TransformState.TRANSFORM_Y, yHandleModel);
        zHandle = new TranslateHandle(COLOR_Z.toIntBits(), TransformState.TRANSFORM_Z, zHandleModel);
        xzPlaneHandle = new TranslateHandle(COLOR_XZ.toIntBits(), TransformState.TRANSFORM_XZ, xzPlaneHandleModel);
        handles.add(xHandle);
        handles.add(yHandle);
        handles.add(zHandle);
        handles.add(xzPlaneHandle);
    }

    @Override
    @NotNull
    public SymbolIcon getIcon() {
        return SymbolIcon.TRANSLATE;
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
    public void render(ModelBatch batch, SceneEnvironment environment, String shaderKey, float delta) {
        super.render(batch, environment, shaderKey, delta);
        if (ctx.getSelectedEntityId() < 0) {
            return;
        }

        batch.begin(ctx.getCurrent().getCamera());
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        xHandle.render(batch, environment, shaderKey, delta);
        yHandle.render(batch, environment, shaderKey, delta);
        zHandle.render(batch, environment, shaderKey, delta);
        xzPlaneHandle.render(batch, environment, shaderKey, delta);
        batch.end();
    }

    @Override
    public void act() {
        super.act();

        if (ctx.getSelectedEntityId() < 0) {
            return;
        }
        scaleHandles();
        translateHandles();
        if (state == TransformState.IDLE) {
            return;
        }

        var ray = ctx.getViewport().getPickRay(Gdx.input.getX(), Gdx.input.getY());

        var positionComponent = ctx.getSelectedEntity().getComponent(PositionComponent.class);
        positionComponent.getLocalPosition(temp0);
        float dst = ctx.getCurrent().getCamera().position.dst(temp0);
        ray.getEndPoint(temp0, dst);

        if (initTranslate) {
            initTranslate = false;
            lastPos.set(temp0);
        }

        boolean modified = true;
        var vec = new Vector3();
        if (state == TransformState.TRANSFORM_XZ) {
            vec.set(temp0.x - lastPos.x, 0, temp0.z - lastPos.z);
        } else if (state == TransformState.TRANSFORM_X) {
            vec.set(temp0.x - lastPos.x, 0, 0);
        } else if (state == TransformState.TRANSFORM_Y) {
            vec.set(0, temp0.y - lastPos.y, 0);
        } else if (state == TransformState.TRANSFORM_Z) {
            vec.set(0, 0, temp0.z - lastPos.z);
        } else {
            modified = false;
        }

        // TODO translation in global space
        // if(globalSpace) {
        // System.out.println("Before: " + vec);
        // System.out.println("After: " + vec);
        // }

        positionComponent.translate(vec);

        if (modified) {
            entityModifiedEvent.setEntityId(ctx.getSelectedEntityId());
            eventBus.post(entityModifiedEvent);
        }

        lastPos.set(temp0);
    }

    @Override
    protected void scaleHandles() {
        if (ctx.getSelectedEntityId() < 0 ||
                ctx.getSelectedEntity().getComponent(PositionComponent.class) == null) {
            return;
        }

        getPositionOfSelectedEntity().getPosition(temp0);
        float scaleFactor = ctx.getCurrent().getCamera().position.dst(temp0) * 0.25f;
        xHandle.getScale().set(scaleFactor * 0.7f, scaleFactor / 2, scaleFactor / 2);
        yHandle.getScale().set(scaleFactor / 2, scaleFactor * 0.7f, scaleFactor / 2);
        zHandle.getScale().set(scaleFactor / 2, scaleFactor / 2, scaleFactor * 0.7f);
        xzPlaneHandle.getScale().set(scaleFactor * 0.13f, scaleFactor * 0.13f, scaleFactor * 0.13f);

        handles.forEach(ToolHandle::applyTransform);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        log.info("Mouse dragged, state: {}", state);
        return state != TransformState.IDLE;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (button != Input.Buttons.LEFT || ctx.getSelectedEntityId() < 0) {
            return false;
        }

        var handle = (TranslateHandle) handlePicker.pick(handles, screenX, screenY);
        if (handle == null) {
            log.info("Translate tool go to state IDLE");
            state = TransformState.IDLE;
            return false;
        }

        state = handle.getState();
        initTranslate = true;
        handle.changeColor(COLOR_SELECTED);

//        command = new TranslateCommand(ctx.getSelectedEntityId());
//        command.setBefore(ctx.getSelectedEntityId().getLocalPosition(temp0));
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

//            command.setAfter(ctx.getSelectedEntityId().getLocalPosition(temp0));
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

    private static class TranslateHandle extends ToolHandle {

        public TranslateHandle(int id, TransformState state, Model model) {
            super(id, state, model);
            modelInstance.getMaterials().first().set(idAttribute);
        }

        @Override
        public void applyTransform() {
            rotation.setEulerAngles(rotationEuler.y, rotationEuler.x, rotationEuler.z);
            modelInstance.transform.set(position, rotation, scale);
        }
    }

}
