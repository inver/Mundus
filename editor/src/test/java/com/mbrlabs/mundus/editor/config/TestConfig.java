package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.texture.TextureService;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ComponentScan({
        "com.mbrlabs.mundus.editor",
        "com.mbrlabs.mundus.editor.events",
        "com.mbrlabs.mundus.editor.assets",
        "com.mbrlabs.mundus.editor.ui",
        "com.mbrlabs.mundus.editor.utils",
        "com.mbrlabs.mundus.editor.tools",
        "com.mbrlabs.mundus.editor.input",
})
@Import({
        UiConfig.class,
})
public class TestConfig {

    @Bean
    public AppEnvironment appEnvironment() {
        return new AppEnvironment() {
            @Override
            public String getHomeDir() {
                return "/tmp/" + UUID.randomUUID() + ".mundus";
            }
        };
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

    @Bean
    public TextureService textureService() {
        return new TextureService();
    }
//
//    @Bean
//    public ModelService modelService() {
//        return new ModelService();
//    }
//
//    @Bean
//    public TerrainService terrainService() {
//        return new TerrainService();
//    }
//
//    @Bean
//    public MaterialService materialService(MetaService metaService) {
//        return new MaterialService(metaService);
//    }
//
//    @Bean
//    public PixmapTextureService pixmapTextureService() {
//        return new PixmapTextureService();
//    }
//
//    @Bean
//    public ModelImporter modelImporter() {
//        return new ModelImporter();
//    }

    @Bean
    public AssetsStorage assetsStorage() {
        return new AssetsStorage(textureService()) {
            @Override
            public FileHandle loadAssetFile(String path) {
                return new FileHandle(path);
            }
        };
    }

    @Bean
    public SceneStorage sceneStorage() {
        return new SceneStorage(mapper(), assetsStorage()) {
            @Override
            protected Skybox createDefaultSkybox() {
                var texture = assetsStorage.loadAssetFile("textures/skybox/default/skybox_default.png");
                return new TestSkyBox();
            }
        };
    }

    @Bean
    public EventBus eventBus() {
        return Mockito.spy(new EventBus());
    }

    @Bean
    public AppUi appUi() {
        var res = mock(AppUi.class);
        when(res.createOpenDialogListener(any())).thenReturn(mock(ClickListener.class));
        return res;
    }

    @Bean
    public PreviewGenerator previewGenerator() {
        return mock(PreviewGenerator.class);
    }

    @Bean
    public ModelBatch modelBatch() {
        return mock(ModelBatch.class);
    }

    @Bean
    public ShapeRenderer shapeRenderer() {
        return mock(ShapeRenderer.class);
    }

    @Bean
    public ShaderStorage shaderStorage() {
        return mock(ShaderStorage.class);
    }

    @Bean
    public UiWidgetsHolder uiWidgetsHolder() {
        return mock(UiWidgetsHolder.class);
    }
}
