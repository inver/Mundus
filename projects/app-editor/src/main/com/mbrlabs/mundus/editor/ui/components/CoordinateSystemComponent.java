package com.mbrlabs.mundus.editor.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableObject;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.builder.ArrowShapeBuilder;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

import static net.nevinsky.abyssus.core.shader.ShaderProvider.DEFAULT_SHADER_KEY;

public class CoordinateSystemComponent implements RenderableObject {
    private static final int START_CONST = 10;
    private static final int LINE_COUNT = 20;
    private static final float ARROW_THICKNESS = 0.1f;
    private static final float ARROW_CAP_SIZE = 0.03f;
    private static final int ARROW_DIVISIONS = 16;

    private final ModelInstance instance;

    public CoordinateSystemComponent() {
        var modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        var partBuilder = modelBuilder.part("line", GL20.GL_LINES,
                (VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked), new Material());
        partBuilder.setColor(Color.GRAY);
        for (int i = 0; i <= LINE_COUNT; i++) {
            partBuilder.line(-START_CONST + i, 0.0f, -START_CONST, -START_CONST + i, 0.0f, START_CONST);
            partBuilder.line(-START_CONST, 0.0f, -START_CONST + i, START_CONST, 0.0f, -START_CONST + i);
        }

        partBuilder = modelBuilder.part("mainLines", GL20.GL_TRIANGLES,
                (VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked), new Material());
        partBuilder.setColor(Color.RED);
        ArrowShapeBuilder.build(partBuilder, -START_CONST - 1, 0f, 0f, START_CONST + 2, 0f, 0f,
                ARROW_CAP_SIZE, ARROW_THICKNESS, ARROW_DIVISIONS);
        partBuilder.setColor(Color.GREEN);
        ArrowShapeBuilder.build(partBuilder, 0f, -START_CONST - 1, 0f, 0f, START_CONST + 2, 0f,
                ARROW_CAP_SIZE, ARROW_THICKNESS, ARROW_DIVISIONS);
        partBuilder.setColor(Color.BLUE);
        ArrowShapeBuilder.build(partBuilder, 0f, 0f, -START_CONST - 1, 0f, 0f, START_CONST + 2,
                ARROW_CAP_SIZE, ARROW_THICKNESS, ARROW_DIVISIONS);

        instance = new ModelInstance(modelBuilder.end());
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        batch.render(instance, DEFAULT_SHADER_KEY);
    }
}
