package com.mbrlabs.mundus.editor.ui.components;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;

public class EditorCameraComponent extends com.mbrlabs.mundus.commons.scene3d.components.CameraComponent {

    private static final long ATTRIBUTES =
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.Normal;
    private static final Material DEFAULT_MATERIAL = new Material();
    private final ModelInstance instance;
    private boolean selected = false;
    private CameraType cameraType = CameraType.FREE;

    public EditorCameraComponent(GameObject go, Camera camera) {
        super(go, camera);
        type = Type.CAMERA;

        var modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        modelBuilder.node().translation.set(0, 0, 1);
        var partBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        BoxShapeBuilder.build(partBuilder, 1, 1, 2);

        modelBuilder.node().rotation.set(Vector3.X, 90);
        partBuilder = modelBuilder.part("cone", GL20.GL_TRIANGLES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        ConeShapeBuilder.build(partBuilder, 1, 1, 1, 16);

        modelBuilder.node();
        partBuilder = modelBuilder.part("directionLine", 1, 3, new Material());
        partBuilder.setColor(Color.GRAY);
        //todo rework for dotted line
        partBuilder.line(0, 0, 0, 10, 0, 0);

        modelBuilder.node().translation.set(0, 0, -10.2f);
        partBuilder = modelBuilder.part("directionHandle", GL20.GL_TRIANGLES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        SphereShapeBuilder.build(partBuilder, 0.4f, 0.4f, 0.4f, 10, 10);

        instance = new ModelInstance(modelBuilder.end());
    }

    public void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
        if (cameraType == CameraType.DIRECTED) {

        } else {

        }
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        if (!selected) {
            instance.transform.set(gameObject.getTransform());
            batch.render(instance);
        }
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }

    public enum CameraType {
        FREE,
        DIRECTED
    }
}
