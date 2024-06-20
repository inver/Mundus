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
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.ScaleCommand;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.model.Model;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import static com.mbrlabs.mundus.editor.tools.TransformTool.TransformState.TRANSFORM_X;
import static com.mbrlabs.mundus.editor.tools.TransformTool.TransformState.TRANSFORM_XYZ;
import static com.mbrlabs.mundus.editor.tools.TransformTool.TransformState.TRANSFORM_Y;
import static com.mbrlabs.mundus.editor.tools.TransformTool.TransformState.TRANSFORM_Z;

/**
 * Scales valid game objects.
 * <p>
 * Game objects with terrain components can't be scaled.
 *
 * @author codenigma, mbrlabs
 * @version 07-10-2016
 */
public class ScaleTool extends TransformTool {

    public static final String NAME = "Scale Tool";

    private final AppUi appUi;

    private final ScaleHandle xHandle;
    private final ScaleHandle yHandle;
    private final ScaleHandle zHandle;
    private final ScaleHandle xyzHandle;

    private final Matrix4 shapeRenderMat = new Matrix4();
    private Viewport viewport3d = null;
    private final Vector3 tempScale = new Vector3();
    private final Vector3 tempScaleDst = new Vector3();

    private final ShapeRenderer shapeRenderer;

    private ScaleCommand command;

    public ScaleTool(EditorCtx ctx, String shaderKey, EntityPicker picker, ToolHandlePicker handlePicker,
                     ShapeRenderer shapeRenderer, CommandHistory history, AppUi appUi,
                     EventBus eventBus) {
        super(ctx, shaderKey, picker, handlePicker, history, eventBus);

        this.shapeRenderer = shapeRenderer;
        this.appUi = appUi;

        ModelBuilder modelBuilder = new ModelBuilder();
        var xPlaneHandleModel = UsefulMeshs.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_X)),
                Vector3.Zero, new Vector3(15, 0, 0));
        var yPlaneHandleModel = UsefulMeshs.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Y)),
                Vector3.Zero, new Vector3(0, 15, 0));
        var zPlaneHandleModel = UsefulMeshs.createArrowStub(new Material(ColorAttribute.createDiffuse(COLOR_Z)),
                Vector3.Zero, new Vector3(0, 0, 15));
        var xyzPlaneHandleModel = modelBuilder.createBox(3, 3, 3,
                new Material(ColorAttribute.createDiffuse(COLOR_XYZ)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        xHandle = new ScaleHandle(X_HANDLE_ID, TRANSFORM_X, xPlaneHandleModel);
        yHandle = new ScaleHandle(Y_HANDLE_ID, TRANSFORM_Y, yPlaneHandleModel);
        zHandle = new ScaleHandle(Z_HANDLE_ID, TRANSFORM_Z, zPlaneHandleModel);
        xyzHandle = new ScaleHandle(XYZ_HANDLE_ID, TRANSFORM_XYZ, xyzPlaneHandleModel);

        handles.add(xHandle);
        handles.add(yHandle);
        handles.add(zHandle);
        handles.add(xyzHandle);
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, String shaderKey, float delta) {
        super.render(batch, environment, shaderKey, delta);

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        if (ctx.getSelectedEntityId() < 0) {
            return;
        }

        batch.begin(ctx.getCurrent().getCamera());
        xHandle.render(batch, environment, shaderKey, delta);
        yHandle.render(batch, environment, shaderKey, delta);
        zHandle.render(batch, environment, shaderKey, delta);
        xyzHandle.render(batch, environment, shaderKey, delta);
        batch.end();

        ctx.getComponentByEntityId(ctx.getSelectedEntityId(), PositionComponent.class)
                .getTransform().getTranslation(temp0);
        if (viewport3d == null) {
            viewport3d = appUi.getSceneWidget().getViewport();
        }

        Vector3 pivot = ctx.getCurrent().getCamera().project(temp0, viewport3d.getScreenX(),
                viewport3d.getScreenY(), viewport3d.getWorldWidth(), viewport3d.getWorldHeight());

        shapeRenderMat.setToOrtho2D(viewport3d.getScreenX(), viewport3d.getScreenY(), viewport3d.getScreenWidth(),
                viewport3d.getScreenHeight());
        switch (state) {
            case TRANSFORM_X:
                renderTool(pivot, COLOR_X);
                break;
            case TRANSFORM_Y:
                renderTool(pivot, COLOR_Y);
                break;
            case TRANSFORM_Z:
                renderTool(pivot, COLOR_Z);
                break;
            case TRANSFORM_XYZ:
                renderTool(pivot, COLOR_XYZ);
                break;
            default:
                break;
        }
    }

    private void renderTool(Vector3 pivot, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.setProjectionMatrix(shapeRenderMat);

        var mouseX = Gdx.input.getX();
        var mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        shapeRenderer.rectLine(pivot.x, pivot.y, mouseX, mouseY, 2);
        shapeRenderer.end();
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
        if (!isScalable(ctx.getSelectedEntityId())) {
            return;
        }

        float dst = getCurrentDst();
        var position = getPositionOfSelectedEntity();
        tempScale.set(position.getLocalScale());

        boolean modified = true;
        if (state == TRANSFORM_X) {
            tempScale.x = (100 / tempScaleDst.x * dst) / 100;
        } else if (state == TRANSFORM_Y) {
            tempScale.y = (100 / tempScaleDst.y * dst) / 100;
        } else if (state == TRANSFORM_Z) {
            tempScale.z = (100 / tempScaleDst.z * dst) / 100;
        } else if (state == TRANSFORM_XYZ) {
            tempScale.x = (100 / tempScaleDst.x * dst) / 100;
            tempScale.y = (100 / tempScaleDst.y * dst) / 100;
            tempScale.z = (100 / tempScaleDst.z * dst) / 100;
        } else {
            modified = false;
        }

        position.getLocalScale()
                .set(tempScale.x, tempScale.y, tempScale.z);

        if (modified) {
            entityModifiedEvent.setEntityId(ctx.getSelectedEntityId());
            eventBus.post(entityModifiedEvent);
        }
    }

    private float getCurrentDst() {
        ctx.getComponentByEntityId(ctx.getSelectedEntityId(), PositionComponent.class)
                .getTransform()
                .getTranslation(temp0);
        var pivot = ctx.getCurrent().getCamera().project(temp0, viewport3d.getScreenX(),
                viewport3d.getScreenY(), viewport3d.getWorldWidth(), viewport3d.getWorldHeight());
        var mouse = temp1.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);

        return MathUtils.dst(pivot.x, pivot.y, mouse.x, mouse.y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if (!isScalable(ctx.getSelectedEntityId())) {
            return false;
        }

        if (button != Input.Buttons.LEFT || ctx.getSelectedEntityId() < 0) {
            return false;
        }
        ScaleHandle handle = (ScaleHandle) handlePicker.pick(handles, screenX, screenY);
        if (handle == null) {
            state = TransformState.IDLE;
            return false;
        }

        var position = ctx.getComponentByEntityId(ctx.getSelectedEntityId(), PositionComponent.class);
        // current scale
        tempScale.set(position.getLocalScale());

        // set tempScaleDst
        tempScaleDst.x = getCurrentDst() / tempScale.x;
        tempScaleDst.y = getCurrentDst() / tempScale.y;
        tempScaleDst.z = getCurrentDst() / tempScale.z;

        if (handle.id == X_HANDLE_ID) {
            state = TransformState.TRANSFORM_X;
            xHandle.changeColor(COLOR_SELECTED);
        } else if (handle.id == Y_HANDLE_ID) {
            state = TransformState.TRANSFORM_Y;
            yHandle.changeColor(COLOR_SELECTED);
        } else if (handle.id == Z_HANDLE_ID) {
            state = TransformState.TRANSFORM_Z;
            zHandle.changeColor(COLOR_SELECTED);
        } else if (handle.id == XYZ_HANDLE_ID) {
            state = TransformState.TRANSFORM_XYZ;
            xyzHandle.changeColor(COLOR_SELECTED);
        }

//
//        // scale command before
//        if (state != TransformState.IDLE) {
//            command = new ScaleCommand(selection);
//            command.setBefore(tempScale);
//        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        super.touchUp(screenX, screenY, pointer, button);
        if (state != TransformState.IDLE) {
            xHandle.changeColor(COLOR_X);
            yHandle.changeColor(COLOR_Y);
            zHandle.changeColor(COLOR_Z);
            xyzHandle.changeColor(COLOR_XYZ);

            // scale command after
//            ctx.getSelectedEntityId().getScale(tempScale);
//            command.setAfter(tempScale);
//            getHistory().add(command);
//            command = null;
            state = TransformState.IDLE;
        }
        return false;
    }

    private boolean isScalable(int entityId) {
        return entityId >= 0 || ctx.getCurrentWorld()
                .getEntity(entityId)
                .getComponent(TypeComponent.class)
                .getType() != TypeComponent.Type.TERRAIN;
    }

    @Override
    protected void scaleHandles() {
        if (ctx.getSelectedEntityId() < 0) {
            return;
        }

        getPositionOfSelectedEntity().getTransform().getTranslation(temp0);
        float scaleFactor = ctx.getCurrent().getCamera().position.dst(temp0) * 0.01f;
        handles.forEach(handle -> {
            handle.getScale().set(scaleFactor, scaleFactor, scaleFactor);
            handle.applyTransform();
        });
    }

    @Override
    @NotNull
    public SymbolIcon getIcon() {
        return SymbolIcon.EXPAND;
    }

    @Override
    public void dispose() {
        super.dispose();
        handles.forEach(Disposable::dispose);
    }

    private static class ScaleHandle extends ToolHandle {

        public ScaleHandle(int id, TransformState state, Model model) {
            super(id, state, model);
            modelInstance.getMaterials().first().set(idAttribute);
        }

        @Override
        public void applyTransform() {
            modelInstance.transform.set(getPosition(), getRotation(), getScale());
        }
    }

}
