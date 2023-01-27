package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.scene3d.components.Renderable;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import com.mbrlabs.mundus.editor.tools.picker.PickerColorEncoder;
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;
import lombok.Getter;

public abstract class ToolHandle implements Disposable, Renderable {

    @Getter
    protected final int id;
    @Getter
    protected final TransformTool.TransformState state;
    protected final Model model;
    protected final ModelInstance modelInstance;
    @Getter
    protected final Vector3 position = new Vector3();
    @Getter
    protected final Vector3 rotationEuler = new Vector3();
    @Getter
    protected final Quaternion rotation = new Quaternion();
    @Getter
    protected final Vector3 scale = new Vector3(1f, 1f, 1f);
    protected PickerIDAttribute idAttribute = new PickerIDAttribute();

    public ToolHandle(int id, TransformTool.TransformState state, Model model) {
        this.id = id;
        this.state = state;
        this.model = model;
        modelInstance = new ModelInstance(model);
        PickerColorEncoder.encodeRaypickColorId(id, idAttribute);
    }

    public abstract void renderPick(ModelBatch modelBatch, ShaderHolder shaders);

    public void act() {

    }

    public abstract void applyTransform();
}
