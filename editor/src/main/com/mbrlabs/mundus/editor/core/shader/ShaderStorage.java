package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.Files;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.ProjectFileChangedEvent;
import groovy.lang.GroovyClassLoader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.BaseShaderProvider;
import net.nevinsky.abyssus.core.shader.ShaderWrapper;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShaderStorage extends BaseShaderProvider {

    private final EditorCtx ctx;
    private final EventBus eventBus;

    @PostConstruct
    public void init() {
        reloadShaders();
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> reloadShaders());
        eventBus.register((ProjectFileChangedEvent.ProjectFileChangedListener) event -> {
            if (!event.getPath().toFile().exists()) {
                throw new NotImplementedException("Implement removing shader");
            }

            var assetName = event.getPath().getParent().getFileName().toString();
            var key = new EditorCtx.AssetKey(AssetType.SHADER, assetName);
            var asset = ctx.getAssetLibrary().get(key);
            if (asset instanceof ShaderAsset) {
                var wrapper = reloadShader((ShaderAsset) asset);
                shaders.put(asset.getName(), wrapper);
            }
        });
    }

    private void reloadShaders() {
        ctx.getAssetLibrary().values().stream()
                .filter(asset -> asset.getType() == AssetType.SHADER)
                .forEach(asset -> get(asset.getName()));
    }

    @Override
    protected ShaderWrapper loadShader(String key) {
        var asset = (ShaderAsset) ctx.getAssetLibrary().get(new EditorCtx.AssetKey(AssetType.SHADER, key));
        if (asset == null) {
            return null;
        }

        return reloadShader(asset);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private EditorShaderWrapper reloadShader(ShaderAsset asset) {
        var classPath = asset.getMeta().getFile().child(asset.getMeta().getAdditional().getShaderClass());

        try (var loader = new GroovyClassLoader(this.getClass().getClassLoader())) {
            var wrapper = (EditorShaderWrapper) shaders.get(asset.getName());
            if (wrapper != null) {
                try {
                    wrapper.getShaderClassLoader().close();
                } catch (Exception e) {
                    log.error("ERROR", e);
                }
            }

            Class<?> clazz;
            if (classPath.type() == Files.FileType.Classpath) {
                clazz = loader.parseClass(new File(
                        getClass().getClassLoader().getResource(classPath.file().getPath()).getFile()
                ));
            } else {
                clazz = loader.parseClass(classPath.file());
            }

            var constructor = clazz.getDeclaredConstructor(String.class, String.class);
            BaseShader shader = (BaseShader) constructor.newInstance(asset.getVertexShader(), asset.getFragmentShader());
            log.info("Loaded shader for {}", asset.getName());

            return new EditorShaderWrapper(loader, shader);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        return null;
    }
}
