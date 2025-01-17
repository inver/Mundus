package com.mbrlabs.mundus.editor.ui.components.handle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.Getter;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

import static net.nevinsky.abyssus.core.shader.ShaderProvider.DEFAULT_SHADER_KEY;

public class DirectionLineRenderDelegate implements RenderableDelegate {
    private static final long ATTRIBUTES = VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked;
    private static final Material DEFAULT_MATERIAL = new Material();
    private final boolean hidden = false;
    @Getter
    private final transient ModelInstance instance;
    @Getter
    private final String shaderKey = DEFAULT_SHADER_KEY;

    public DirectionLineRenderDelegate() {
        var modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        var partBuilder = modelBuilder.part("directionLine", GL20.GL_LINES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        //todo rework for dotted line
        partBuilder.line(0, 0, 0, 0, 0, 10.2f);

        instance = new ModelInstance(modelBuilder.end());
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        if (!hidden) {
            batch.render(instance, shaderKey);
        }
    }

    @Override
    public void set2PointPosition(Vector3 point1, Vector3 point2) {
        var mesh = instance.model.getMeshes().iterator().next();
        final int stride = mesh.getVertexSize() / 4;

        final float[] vertices = new float[mesh.getNumVertices() * stride];
        mesh.getVertices(0, mesh.getNumVertices() * stride, vertices);

        vertices[0] = point1.x;
        vertices[1] = point1.y;
        vertices[2] = point1.z;
        vertices[stride] = point2.x;
        vertices[stride + 1] = point2.y;
        vertices[stride + 2] = point2.z;

        mesh.updateVertices(0, vertices);
    }
}
