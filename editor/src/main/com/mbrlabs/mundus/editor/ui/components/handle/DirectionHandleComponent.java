package com.mbrlabs.mundus.editor.ui.components.handle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import lombok.Getter;

public class DirectionHandleComponent extends AbstractComponent {

    private static final long ATTRIBUTES =
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.Normal;
    private static final Material DEFAULT_MATERIAL = new Material();
    private final ModelInstance instance;
    private boolean hidden = false;

    @Getter
    private final ModelInstance handleLineInstance;

    public DirectionHandleComponent(GameObject go) {
        super(go);

        var modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        var partBuilder = modelBuilder.part("directionHandle", GL20.GL_TRIANGLES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        SphereShapeBuilder.build(partBuilder, 0.4f, 0.4f, 0.4f, 10, 10);
        instance = new ModelInstance(modelBuilder.end());

        modelBuilder.begin();
        partBuilder = modelBuilder.part("directionLine", GL20.GL_LINES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        //todo rework for dotted line
        partBuilder.line(0, 0, 0, 0, 0, -1);

        handleLineInstance = new ModelInstance(modelBuilder.end());
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        if (!hidden) {
            instance.transform.set(gameObject.getTransform());
            batch.render(instance);

            //transform set in
            batch.render(handleLineInstance);
        }
    }
}
