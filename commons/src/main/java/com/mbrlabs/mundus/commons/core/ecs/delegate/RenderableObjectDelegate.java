package com.mbrlabs.mundus.commons.core.ecs.delegate;

import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.core.ModelBatch;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenderableObjectDelegate implements RenderableDelegate {
    private RenderableSceneObject asset;
    private String shaderName;

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        batch.render(asset, environment, shaders.get(shaderName));
    }

    @Override
    public void setPosition(Matrix4 position) {
        asset.getModelInstance().transform.set(position);
    }
}
