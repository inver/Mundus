package com.mbrlabs.mundus.editor.core.shader;

import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.shader.AbstractShaderProvider;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.Shader;
import net.nevinsky.abyssus.core.shader.ShaderConfig;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class EditorShaderProvider extends AbstractShaderProvider<EditorShaderHolder> {

    private final ShaderClassLoader shaderClassLoader;
    private final Function<String, ShaderAsset> shaderAssetGetter;

    protected EditorShaderHolder createHolder(String key) {
        var asset = shaderAssetGetter.apply(key);
        if (asset == null) {
            return null;
        }
        return new EditorShaderHolder(key, shaderClassLoader.createShaderClass(asset));
    }

    @Override
    protected Shader createShader(EditorShaderHolder holder, Renderable renderable) {
        var asset = shaderAssetGetter.apply(holder.getKey());

        var shaderConfig = new ShaderConfig();
        shaderConfig.setFragmentShader(asset.getFragmentShader());
        shaderConfig.setVertexShader(asset.getVertexShader());

        var clazz = holder.getShaderClass();
        try {
            var constructor = clazz.getDeclaredConstructor(ShaderConfig.class, Renderable.class);
            var shader = (BaseShader) constructor.newInstance(shaderConfig, renderable);
            log.info("Loaded shader for {}", asset.getName());

            return shader;
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        return null;
    }

    public void remove(String key) {
        shaderCache.remove(key);
    }

    public void clear() {
        shaderCache.clear();
    }
}
