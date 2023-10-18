package com.mbrlabs.mundus.commons.core.ecs.delegate;

import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RenderableObjectDelegate implements RenderableDelegate {
    private RenderableSceneObject asset;
    private String shaderKey;

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        batch.render(asset, environment, shaderKey);
    }

    @Override
    public void setPosition(Matrix4 position) {
        asset.getModelInstance().transform.set(position);
    }
}
