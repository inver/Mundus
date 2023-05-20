package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.assets.AppFileHandle;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderMeta;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.shader.BaseShader;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import static net.nevinsky.abyssus.core.shader.ShaderProvider.DEFAULT_SHADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShaderStorageTest {

    private static final String BUNDLED_SHADER_ASSET = "bundledShaderAsset";
    public static final String OVERRIDE_SHADER = "overrideShader";
    private ShaderStorage shaderStorage;

    private final EditorCtx ctx = new EditorCtx();
    private final EventBus eventBus = new EventBus();
    private final ShaderAssetLoader shaderAssetLoader = new ShaderAssetLoader();
    private final ShaderClassLoader shaderClassLoader = mock(ShaderClassLoader.class);

    private final BaseShader defaultShader = mock(BaseShader.class);
    private final BaseShader bundledShader = mock(BaseShader.class);
    private final BaseShader projectShader = mock(BaseShader.class);

    @BeforeEach
    public void init() {
        initShaderClassLoader(DEFAULT_SHADER, defaultShader);
        initShaderClassLoader(BUNDLED_SHADER_ASSET, bundledShader);

        var meta = createShaderMeta("./" + BUNDLED_SHADER_ASSET);
        ctx.getAssetLibrary().put(new AssetKey(AssetType.SHADER, BUNDLED_SHADER_ASSET), shaderAssetLoader.load(meta));
        meta = createShaderMeta("./" + DEFAULT_SHADER);
        ctx.getAssetLibrary().put(new AssetKey(AssetType.SHADER, DEFAULT_SHADER), shaderAssetLoader.load(meta));

        shaderStorage = new ShaderStorage(ctx, eventBus, shaderAssetLoader, shaderClassLoader);
    }

    @Test
    public void testLoadingBundled() {
        shaderStorage.init();
        Assertions.assertEquals(bundledShader, shaderStorage.get(BUNDLED_SHADER_ASSET));
    }

    @Test
    public void testProjectShaders() {
        initShaderClassLoader(OVERRIDE_SHADER, projectShader);

        var meta = createShaderMeta("./testProject/assets/overrideShader");
        shaderStorage.init();

        var project = new ProjectContext(new PerspectiveCamera());
        project.getProjectAssets().put(new AssetKey(AssetType.SHADER, OVERRIDE_SHADER),
                shaderAssetLoader.load(meta));
        ctx.setCurrent(project);

        eventBus.post(new ProjectChangedEvent(project));

        var shader = shaderStorage.get(BUNDLED_SHADER_ASSET);
        Assertions.assertEquals(bundledShader, shader);
        shader = shaderStorage.get(OVERRIDE_SHADER);
        Assertions.assertEquals(projectShader, shader);
    }

    @NotNull
    private static Meta<ShaderMeta> createShaderMeta(String fileName) {
        var meta = new Meta<ShaderMeta>();
        meta.setType(AssetType.SHADER);
        meta.withFile(new AppFileHandle(fileName, Files.FileType.Classpath));
        var additional = new ShaderMeta();
        additional.setFragment("default.frag.glsl");
        additional.setVertex("default.vert.glsl");
        additional.setShaderClass("Shader.groovy");
        meta.setAdditional(additional);
        return meta;
    }

    private void initShaderClassLoader(String shaderName, BaseShader shader) {
        var projectHolder = mock(EditorShaderHolder.class);
        when(projectHolder.getDefaultInstance()).thenReturn(shader);
        when(shaderClassLoader.reloadShader(argThat(new AssetNameMatcher(shaderName)), any())).thenReturn(
                projectHolder);
    }

    @RequiredArgsConstructor
    private static class AssetNameMatcher implements ArgumentMatcher<ShaderAsset> {
        private final String name;

        @Override
        public boolean matches(ShaderAsset argument) {
            if (argument == null) {
                return false;
            }
            return name.equals(argument.getName());
        }
    }
}
