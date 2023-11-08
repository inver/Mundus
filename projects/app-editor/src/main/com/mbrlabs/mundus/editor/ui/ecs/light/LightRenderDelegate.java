package com.mbrlabs.mundus.editor.ui.ecs.light;

import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

@RequiredArgsConstructor
@Getter
public class LightRenderDelegate implements RenderableDelegate {
    private final transient ModelInstance modelInstance;
    private final String shaderKey;

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, String shaderKey, float delta) {
        batch.render(modelInstance, environment, shaderKey);
    }

    @Override
    public void setPosition(Matrix4 position) {
        modelInstance.transform.set(position);
    }
}
