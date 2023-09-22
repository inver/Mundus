package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.ProjectFileChangedEvent;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.shader.Shader;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mbrlabs.mundus.commons.assets.AssetConstants.META_FILE_NAME;

@Slf4j
@Service
public class ShaderStorage implements ShaderProvider {

    private final EditorShaderProvider baseProvider;
    private final EditorShaderProvider projectShaderProvider;
    private final EventBus eventBus;
    private final ProjectManager projectManager;

    public ShaderStorage(EditorCtx ctx, ShaderClassLoader shaderClassLoader, EventBus eventBus,
                         ProjectManager projectManager) {
        this.eventBus = eventBus;
        this.projectManager = projectManager;
        baseProvider = new EditorShaderProvider(
                shaderClassLoader, (key) -> ctx.getAsset(new AssetKey(AssetType.SHADER, key))
        );
        projectShaderProvider = new EditorShaderProvider(
                shaderClassLoader, (key) -> {
            if (ctx.getCurrent() == null) {
                return null;
            }
            return ctx.getCurrent().getAsset(new AssetKey(AssetType.SHADER, key));
        });
    }

    @PostConstruct
    public void init() {
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            projectShaderProvider.clear();
        });
        eventBus.register((ProjectFileChangedEvent.ProjectFileChangedListener) event -> {
            var assetFolderPath = event.getPath().getParent();
            projectShaderProvider.remove(assetFolderPath.getFileName().toString());

            projectManager.reloadAsset(
                    new AssetKey(AssetType.SHADER, assetFolderPath.getFileName().toString()),
                    new FileHandle(assetFolderPath.toFile())
            );
        });
    }


    @Override
    public Shader get(String key, Renderable renderable) {
        var res = projectShaderProvider.get(key, renderable);
        if (res != null) {
            return res;
        }
        res = baseProvider.get(key, renderable);
        if (res != null) {
            return res;
        }
        res = baseProvider.get(DEFAULT_SHADER_KEY, renderable);
        if (res != null) {
            return res;
        }

        throw new RuntimeException("Could not find shader for renderable. Even default shader doesn't accept it.");
    }

    @Override
    public void dispose() {
        projectShaderProvider.dispose();
        baseProvider.dispose();
    }
}
