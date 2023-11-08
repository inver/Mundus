package com.mbrlabs.mundus.commons.core.ecs.behavior;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;

@Slf4j
@All(RenderComponent.class)
public class RenderComponentSystem extends IteratingSystem {
    private ModelBatch batch;
    private SceneEnvironment environment;
    protected ComponentMapper<RenderComponent> mapper;

    @Override
    protected void process(int entityId) {
        if (batch != null) {
            mapper.get(entityId).getRenderable().render(batch, environment, world.getDelta());
        }
    }

    public void setRenderData(ModelBatch batch, SceneEnvironment environment) {
        this.batch = batch;
        this.environment = environment;
    }
}
