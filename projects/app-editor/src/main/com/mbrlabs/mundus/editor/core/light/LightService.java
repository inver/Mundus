package com.mbrlabs.mundus.editor.core.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.core.ecs.component.LightComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.editor.core.ecs.EcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.ui.components.ModelCreator;
import com.mbrlabs.mundus.editor.ui.components.light.LightRenderDelegate;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.builder.ConeShapeBuilder;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.springframework.stereotype.Component;

@Component
public class LightService implements ModelCreator {

    private static final long ATTRIBUTES =
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.Normal;
    //todo move material to some holder of default materials
    private static final Material DEFAULT_MATERIAL = new Material();

    private final EditorCtx ctx;
    private final EcsService ecsService;
    private final Model lightBody;

    public LightService(EditorCtx ctx, EcsService ecsService) {
        this.ctx = ctx;
        this.ecsService = ecsService;
        lightBody = createLightBodyModel();
    }

    private Model createLightBodyModel() {
        var modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        modelBuilder.node().rotation.set(Vector3.X, 90);
        var partBuilder = modelBuilder.part("cone", GL20.GL_TRIANGLES, ATTRIBUTES, DEFAULT_MATERIAL);
        partBuilder.setColor(Color.GRAY);
        ConeShapeBuilder.build(partBuilder, 1, 1, 1, 16);

        return modelBuilder.end();
    }

    public int createDirectionLight(int parentId) {
        var light = new DirectionalLight();

        var entityId = ecsService.createEntityWithDirection(ctx.getCurrentWorld(), parentId, new Vector3(0, 10f, 0),
                Vector3.Zero, "Direction light",
                new LightRenderDelegate(new ModelInstance(lightBody), ShaderProvider.DEFAULT_SHADER_KEY),
                new LightRenderDelegate(new ModelInstance(lightBody), ShaderProvider.DEFAULT_SHADER_KEY),
                new LightComponent(light),
                new TypeComponent(TypeComponent.Type.LIGHT_DIRECTIONAL)
        );

        //todo check this
//        ctx.getCurrent().getCurrentScene().getEnvironment().add(light);
        return entityId;
    }

    @Override
    public RenderableDelegate createRenderableDelegate(String shaderKey) {
        return new LightRenderDelegate(new ModelInstance(lightBody), shaderKey);
    }

    @Override
    public String getClazz() {
        return LightRenderDelegate.class.getName();
    }
}
