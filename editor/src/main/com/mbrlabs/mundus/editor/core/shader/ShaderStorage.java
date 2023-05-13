package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.Files;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderMeta;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
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
    private final ShaderAssetLoader shaderAssetLoader;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        loadAllBundledShaders();
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            ctx.getCurrent().getProjectAssets().values().stream()
                    .filter(asset -> asset.getType() == AssetType.SHADER)
                    .forEach(asset -> get(asset.getName()));
        });
        eventBus.register((ProjectFileChangedEvent.ProjectFileChangedListener) event -> {
            var assetFolderPath = event.getPath().getParent();
            if (!assetFolderPath.toFile().exists()) {
                throw new NotImplementedException("Implement removing shader");
            }

            var assetKey = new AssetKey(AssetType.SHADER, assetFolderPath.getFileName().toString());
            var asset = ctx.getCurrent().getProjectAssets().get(assetKey);
            if (asset != null) {
                asset = shaderAssetLoader.load((Meta<ShaderMeta>) asset.getMeta());
                ctx.getCurrent().getProjectAssets().put(assetKey, asset);
                var wrapper = reloadShader((ShaderAsset) asset);
                shaders.put(asset.getName(), wrapper);
            }
        });
    }

    private void loadAllBundledShaders() {
        ctx.getAssetLibrary().values().stream()
                .filter(asset -> asset.getType() == AssetType.SHADER)
                .forEach(asset -> get(asset.getName()));
    }

    @Override
    protected ShaderWrapper loadBundledShader(String key) {
        var assetKey = new AssetKey(AssetType.SHADER, key);
        var asset = (ShaderAsset) ctx.getAssetLibrary().get(assetKey);
        if (asset == null) {
            return null;
        }

        return reloadShader(asset);
    }

    @Override
    protected ShaderWrapper loadProjectShader(String key) {
        if (ctx.getCurrent() == null) {
            return null;
        }

        var assetKey = new AssetKey(AssetType.SHADER, key);
        var asset = (ShaderAsset) ctx.getCurrent().getProjectAssets().get(assetKey);
        if (asset == null) {
            return null;
        }
        return reloadShader(asset);
    }

    /**
     * @param asset actual loaded shader asset
     * @return new instance of wrapper with full reloaded shader
     */
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
            var shader = (BaseShader) constructor.newInstance(asset.getVertexShader(), asset.getFragmentShader());
            log.info("Loaded shader for {}", asset.getName());

            return new EditorShaderWrapper(loader, shader);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        return null;
    }
}
