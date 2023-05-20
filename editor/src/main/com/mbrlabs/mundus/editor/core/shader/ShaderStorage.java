package com.mbrlabs.mundus.editor.core.shader;

import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.ProjectFileChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.shader.BaseShaderProvider;
import net.nevinsky.abyssus.core.shader.ShaderHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShaderStorage extends BaseShaderProvider {

    private final EditorCtx ctx;
    private final EventBus eventBus;
    private final ShaderAssetLoader shaderAssetLoader;
    private final ShaderClassLoader shaderClassLoader;

    private final Map<String, ShaderHolder> projectShaders = new ConcurrentHashMap<>();

    @PostConstruct
    @Override
    public void init() {
        super.init();
        loadAllBundledShaders();
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            projectShaders.clear();
            ctx.getCurrent().getProjectAssets().values().stream()
                    .filter(asset -> asset.getType() == AssetType.SHADER)
                    .forEach(asset -> loadShaderAssetAndCache(projectShaders, (ShaderAsset) asset));
        });
        eventBus.register((ProjectFileChangedEvent.ProjectFileChangedListener) event -> {
            var assetFolderPath = event.getPath().getParent();
            if (!assetFolderPath.toFile().exists()) {
                log.debug("Remove shader '{}'", assetFolderPath.getFileName().toString());
                projectShaders.remove(assetFolderPath.getFileName().toString());
                return;
            }

            var assetKey = new AssetKey(AssetType.SHADER, assetFolderPath.getFileName().toString());
            ShaderAsset asset = (ShaderAsset) ctx.getCurrent().getProjectAssets().get(assetKey);
            if (asset != null) {
                asset = shaderAssetLoader.load(asset.getMeta());
                // put updated content of shader to project assets
                ctx.getCurrent().getProjectAssets().put(assetKey, asset);
                loadShaderAssetAndCache(projectShaders, asset);
            }
        });
    }

    private void loadAllBundledShaders() {
        ctx.getAssetLibrary().values().stream()
                .filter(asset -> asset.getType() == AssetType.SHADER)
                .forEach(asset -> loadShaderAssetAndCache(shaders, (ShaderAsset) asset));
    }

    @Override
    protected ShaderHolder loadShaderAndCache(String key) {
        var assetKey = new AssetKey(AssetType.SHADER, key);
        ShaderAsset asset;
        if (ctx.getCurrent() != null) {
            if (projectShaders.containsKey(key)) {
                return projectShaders.get(key);
            }

            asset = (ShaderAsset) ctx.getCurrent().getProjectAssets().get(assetKey);
            if (asset != null) {
                var res = loadShaderAssetAndCache(projectShaders, asset);
                if (res != null) {
                    return res;
                }
            }
        }

        if (shaders.containsKey(key)) {
            return shaders.get(key);
        }

        asset = (ShaderAsset) ctx.getAssetLibrary().get(assetKey);
        if (asset == null) {
            return null;
        }

        return loadShaderAssetAndCache(shaders, asset);
    }

    private EditorShaderHolder loadShaderAssetAndCache(Map<String, ShaderHolder> shaders, ShaderAsset asset) {
        var res = shaderClassLoader.reloadShader(asset, shaders.get(asset.getName()));
        if (res != null) {
            shaders.put(asset.getName(), res);
            return res;
        }
        return null;
    }
}
