package com.mbrlabs.mundus.editor.ui.ecs.camera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.core.ecs.component.CameraComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.editor.core.ecs.EcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.ui.ecs.ModelCreator;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.builder.BoxShapeBuilder;
import net.nevinsky.abyssus.core.builder.ConeShapeBuilder;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.springframework.stereotype.Component;

@Component
public class CameraService implements ModelCreator {
    private static final long ATTRIBUTES =
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.Normal;
    private static final Material DEFAULT_MATERIAL = new Material();

    private final EditorCtx ctx;
    private final EcsService ecsService;

    private final Model cameraBody;

    public CameraService(EditorCtx ctx, EcsService ecsService) {
        this.ctx = ctx;
        this.ecsService = ecsService;
        cameraBody = createCameraBodyModel();
    }

    private Model createCameraBodyModel() {
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

        return modelBuilder.end();
    }

    public int createEcsCamera(int parentId) {
        var world = ctx.getCurrent().getCurrentScene().getWorld();

        return ecsService.createEntityWithDirection(world, parentId, new Vector3(0, 0, 10.2f), Vector3.Zero,
                "Camera",
                new CameraBodyRenderDelegate(new ModelInstance(cameraBody), ShaderProvider.DEFAULT_SHADER_KEY),
                new CameraBodyRenderDelegate(new ModelInstance(cameraBody), ShaderProvider.DEFAULT_SHADER_KEY),
                new CameraComponent(),
                new TypeComponent(TypeComponent.Type.CAMERA)
        );
    }

    @Override
    public RenderableDelegate createRenderableDelegate(String shaderKey) {
        return new CameraBodyRenderDelegate(new ModelInstance(cameraBody), shaderKey);
    }

    @Override
    public String getClazz() {
        return CameraBodyRenderDelegate.class.getName();
    }
}
