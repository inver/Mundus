package com.mbrlabs.mundus.editor.ui.components.light;

import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

@RequiredArgsConstructor
public class LightRenderDelegate implements RenderableDelegate {
    private final transient ModelInstance instance;
    @Getter
    private final String shaderKey;

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        batch.render(instance, environment, shaderKey);
    }

    @Override
    public void setPosition(Matrix4 position) {
        instance.transform.set(position);
    }
}
