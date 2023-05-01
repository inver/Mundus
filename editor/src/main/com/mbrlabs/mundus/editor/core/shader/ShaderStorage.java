package com.mbrlabs.mundus.editor.core.shader;

import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.shaders.SkyboxShader;
import com.mbrlabs.mundus.commons.shaders.TerrainShader;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent;
import com.mbrlabs.mundus.editor.shader.MaterialPreviewShader;
import com.mbrlabs.mundus.editor.shader.WireframeShader;
import com.mbrlabs.mundus.editor.tools.picker.PickerShader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.shader.BaseShader;
import net.nevinsky.abyssus.core.shader.DefaultShader;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_FRAG;
import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_VERT;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShaderStorage implements ShaderProvider {

    private final EditorCtx ctx;
    private final EventBus eventBus;
    private final Registry registry;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile Future<?> watchFuture = null;
    private volatile Boolean autoReloadFromDisk = false;
    private final Map<String, BaseShader> shaders = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        autoReloadFromDisk = registry.getSettings().isReloadShaderFromDisk();
        eventBus.register((SettingsChangedEvent.SettingsChangedListener) event -> {
            autoReloadFromDisk = event.getSettings().isReloadShaderFromDisk();
            toggleAutoReload();
        });
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> toggleAutoReload());
    }

    public Map<String, BaseShader> load(ProjectRef ref) {
        var path = ref.getPath() + ProjectConstants.PROJECT_SHADERS_DIR;
        log.warn("Loaded only bundled shaders!");
        //todo load from folder
        return loadDefault();
    }

    public Map<String, BaseShader> loadDefault() {
        var res = new HashMap<String, BaseShader>();
        res.put(ProjectConstants.SHADER_MATERIAL_PREVIEW,
                new MaterialPreviewShader(SHADER_MATERIAL_PREVIEW_VERT, SHADER_MATERIAL_PREVIEW_FRAG));
        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseShader> T get(String key) {
        var res = shaders.get(key);
        if (res != null) {
            return (T) res;
        }

        res = loadShader(key);
        if (res != null) {
            res.init();
            shaders.put(key, res);
            return (T) res;
        }

        return (T) shaders.get(ShaderConstants.DEFAULT);
    }

    private BaseShader loadShader(String key) {
        switch (key) {
            case ShaderConstants.DEFAULT: {
                var asset = (ShaderAsset) ctx.getAssetLibrary().get(ProjectConstants.SHADER_DEFAULT_PATH);
                return new DefaultShader(asset.getVertexShader(), asset.getFragmentShader());
            }
            case ShaderConstants.SKYBOX: {
                var asset = (ShaderAsset) ctx.getAssetLibrary().get(ProjectConstants.SHADER_SKYBOX_PATH);
                return new SkyboxShader(asset.getVertexShader(), asset.getFragmentShader());
            }
            case ShaderConstants.WIREFRAME: {
                var asset = (ShaderAsset) ctx.getAssetLibrary().get(ProjectConstants.SHADER_WIREFRAME_PATH);
                return new WireframeShader(asset.getVertexShader(), asset.getFragmentShader());
            }
            case ShaderConstants.TERRAIN: {
                var asset = (ShaderAsset) ctx.getAssetLibrary().get(ProjectConstants.SHADER_TERRAIN_PATH);
                return new TerrainShader(asset.getVertexShader(), asset.getFragmentShader());
            }
            case ShaderConstants.PICKER: {
                var asset = (ShaderAsset) ctx.getAssetLibrary().get(ProjectConstants.SHADER_PICKER_PATH);
                return new PickerShader(asset.getVertexShader(), asset.getFragmentShader());
            }
            default:
                return null;
        }
    }

    public void toggleAutoReload() {
        if (!autoReloadFromDisk && watchFuture != null) {
            watchFuture.cancel(true);
            watchFuture = null;
        } else if (autoReloadFromDisk && watchFuture == null) {
            watchFuture = executorService.submit(new FileWatcher(ctx.getCurrent().path, shaders::remove));
        } else {
            // project changed, stop old file watcher and start new
            watchFuture.cancel(true);
            watchFuture = executorService.submit(new FileWatcher(ctx.getCurrent().path, shaders::remove));
        }
    }

    public void dispose() {
        executorService.shutdownNow();
    }
}
