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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EntityModifiedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.tools.picker.EntityPicker;
import com.mbrlabs.mundus.editor.tools.picker.ToolHandlePicker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 08-03-2016
 */
public abstract class TransformTool extends SelectionTool {

    public enum TransformState {
        TRANSFORM_X, TRANSFORM_Y, TRANSFORM_Z, TRANSFORM_XZ, TRANSFORM_XYZ, IDLE
    }

    protected static Color COLOR_X = Color.RED;
    protected static Color COLOR_Y = Color.GREEN;
    protected static Color COLOR_Z = Color.BLUE;
    protected static Color COLOR_XZ = Color.CYAN;
    protected static Color COLOR_XYZ = Color.LIGHT_GRAY;
    protected static Color COLOR_SELECTED = Color.YELLOW;

    protected static final int X_HANDLE_ID = COLOR_X.toIntBits();
    protected static final int Y_HANDLE_ID = COLOR_Y.toIntBits();
    protected static final int Z_HANDLE_ID = COLOR_Z.toIntBits();
    protected static final int XZ_HANDLE_ID = COLOR_XZ.toIntBits();
    protected static final int XYZ_HANDLE_ID = 4;

    protected final Vector3 temp0 = new Vector3();
    protected final Vector3 temp1 = new Vector3();

    protected ToolHandlePicker handlePicker;
    protected EntityModifiedEvent entityModifiedEvent = new EntityModifiedEvent(-1);

    protected final List<ToolHandle> handles = new ArrayList<>();
    protected TransformState state = TransformState.IDLE;

    public TransformTool(EditorCtx ctx, String shaderKey, EntityPicker picker, ToolHandlePicker handlePicker,
                         CommandHistory history, EventBus eventBus) {
        super(ctx, shaderKey, picker, history, eventBus);
        this.handlePicker = handlePicker;
    }

    @Override
    public void entitySelected(int entityId) {
        super.entitySelected(entityId);

        rotateHandles();
        scaleHandles();
        translateHandles();
    }

    protected abstract void scaleHandles();

    protected void translateHandles() {
        if (getCtx().getSelectedEntityId() < 0 ||
                getCtx().getSelectedEntity().getComponent(PositionComponent.class) == null) {
            return;
        }

        getCtx().getSelectedEntity().getComponent(PositionComponent.class).getPosition(temp0);
        handles.forEach(handle -> {
            handle.getPosition().set(temp0);
            handle.applyTransform();
        });
    }

    protected void rotateHandles() {
        //do nothing by default
    }

}
