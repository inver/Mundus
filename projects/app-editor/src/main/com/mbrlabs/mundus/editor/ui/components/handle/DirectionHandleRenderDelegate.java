package com.mbrlabs.mundus.editor.ui.components.handle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.builder.SphereShapeBuilder;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

public class DirectionHandleRenderDelegate implements RenderableDelegate {
    private static final long ATTRIBUTES =
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.Normal;
    private static final Material DEFAULT_MATERIAL = new Material();
    private final transient ModelInstance instance;
    private final boolean hidden = false;
    private final String shaderKey;

    public DirectionHandleRenderDelegate(String shaderKey) {
        this.shaderKey = shaderKey;

        var modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        var partBuilder = modelBuilder.part("directionHandle", GL20.GL_TRIANGLES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        SphereShapeBuilder.build(partBuilder, 0.4f, 0.4f, 0.4f, 10, 10);
        instance = new ModelInstance(modelBuilder.end());
    }

    public DirectionHandleRenderDelegate() {
        this(ShaderProvider.DEFAULT_SHADER);
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        if (!hidden) {
            batch.render(instance, shaderKey);
        }
    }

    @Override
    public void setPosition(Matrix4 position) {
        instance.transform.set(position);
    }
}
