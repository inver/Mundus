package com.mbrlabs.mundus.editor.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableObject;
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;
import com.mbrlabs.mundus.editor.utils.PickerColorEncoder;
import lombok.Getter;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.model.Model;

public abstract class ToolHandle implements Disposable, RenderableObject {

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
        PickerColorEncoder.encodeRayPickColorId(id, idAttribute);
    }

    public void changeColor(Color color) {
        var diffuse = (ColorAttribute) modelInstance.getMaterials().get(0).get(ColorAttribute.Diffuse);
        diffuse.color.set(color);
    }

    public abstract void applyTransform();

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, String shaderKey, float delta) {
        batch.render(modelInstance, shaderKey);
    }

    @Override
    public void dispose() {
        model.dispose();
    }
}
