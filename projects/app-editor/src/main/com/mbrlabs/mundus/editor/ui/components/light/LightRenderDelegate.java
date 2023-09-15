package com.mbrlabs.mundus.editor.ui.components.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.builder.ConeShapeBuilder;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

import static net.nevinsky.abyssus.core.shader.ShaderProvider.DEFAULT_SHADER_KEY;

public class LightRenderDelegate implements RenderableDelegate {

    private static final long ATTRIBUTES =
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.Normal;
    private static final Material DEFAULT_MATERIAL = new Material();
    private final transient ModelInstance instance;

    public LightRenderDelegate() {
        var modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        modelBuilder.node().rotation.set(Vector3.X, 90);
        var partBuilder = modelBuilder.part("cone", GL20.GL_TRIANGLES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        ConeShapeBuilder.build(partBuilder, 1, 1, 1, 16);

        instance = new ModelInstance(modelBuilder.end());
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        batch.render(instance, DEFAULT_SHADER_KEY);
    }

    @Override
    public void setPosition(Matrix4 position) {
        instance.transform.set(position);
    }
}
