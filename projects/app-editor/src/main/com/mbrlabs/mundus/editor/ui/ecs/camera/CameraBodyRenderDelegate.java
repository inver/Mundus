package com.mbrlabs.mundus.editor.ui.ecs.camera;

import com.badlogic.gdx.math.Matrix4;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

@RequiredArgsConstructor
public class CameraBodyRenderDelegate implements RenderableDelegate {
    private final transient ModelInstance instance;
    @Getter
    private final String shaderKey;

    @Override
    public void setPosition(Matrix4 position) {
        instance.transform.set(position);
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        batch.render(instance, environment, shaderKey);
    }
}
