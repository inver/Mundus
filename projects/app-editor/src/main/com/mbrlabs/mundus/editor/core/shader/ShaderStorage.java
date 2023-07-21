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
import net.nevinsky.abyssus.core.Renderable;
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
//        loadAllBundledShaders();
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            projectShaders.clear();
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
//                loadShaderAssetAndCache(projectShaders, asset);
            }
        });
    }

    private void loadAllBundledShaders() {
//        ctx.getAssetLibrary().values().stream()
//                .filter(asset -> asset.getType() == AssetType.SHADER)
//                .forEach(asset -> loadShaderAssetAndCache(shaders, (ShaderAsset) asset));
    }

    @Override
    protected ShaderHolder loadShaderAndCache(String key, Renderable renderable) {
        var assetKey = new AssetKey(AssetType.SHADER, key);
        var compositeKey = createCompositeKey(key, renderable);

        ShaderAsset asset;

        //check project shaders
        if (ctx.getCurrent() != null) {
            if (projectShaders.containsKey(compositeKey)) {
                return projectShaders.get(key);
            }

            asset = (ShaderAsset) ctx.getCurrent().getProjectAssets().get(assetKey);
            if (asset != null) {
                var res = loadShaderAssetAndCache(projectShaders, asset, renderable);
                if (res != null) {
                    return res;
                }
            }
        }

        // check bundled shaders
        var shader = shaders.get(compositeKey);
        if (shader != null) {
            return shader;
        }

        asset = (ShaderAsset) ctx.getAssetLibrary().get(assetKey);
        if (asset == null) {
            return null;
        }

        return loadShaderAssetAndCache(shaders, asset, renderable);
    }

    private EditorShaderHolder loadShaderAssetAndCache(Map<String, ShaderHolder> shaders, ShaderAsset asset,
                                                       Renderable renderable) {
        var res = shaderClassLoader.reloadShader(asset, shaders.get(asset.getName()));
        if (res == null) {
            return null;
        }

        var compositeKey = createCompositeKey(asset.getName(), renderable);
        if (renderable == null) {
            shaders.put(compositeKey, res);
            return res;
        }

        if (res.getDefaultInstance().canRender(renderable)) {
            var renderableShader = res.clone();
            renderableShader.init(renderable);
            shaders.put(compositeKey, renderableShader);
            return renderableShader;
        }

        return null;
    }
}
