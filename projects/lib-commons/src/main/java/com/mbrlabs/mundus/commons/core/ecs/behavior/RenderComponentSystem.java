package com.mbrlabs.mundus.commons.core.ecs.behavior;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

@Slf4j
@All(RenderComponent.class)
public class RenderComponentSystem extends IteratingSystem {
    private ModelBatch batch;
    private SceneEnvironment environment;
    private ShaderProvider shaders;
    protected ComponentMapper<RenderComponent> mapper;

    @Override
    protected void process(int entityId) {
        if (batch != null) {
            mapper.get(entityId).getRenderable().render(batch, environment, shaders, world.getDelta());
        }
    }

    public void setRenderData(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders) {
        this.batch = batch;
        this.environment = environment;
        this.shaders = shaders;
    }
}
