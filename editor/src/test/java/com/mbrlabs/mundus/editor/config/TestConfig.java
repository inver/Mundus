package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.material.MaterialService;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelService;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureService;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainService;
import com.mbrlabs.mundus.commons.assets.texture.TextureService;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ComponentScan({
        "com.mbrlabs.mundus.editor.core",
        "com.mbrlabs.mundus.editor.events",
        "com.mbrlabs.mundus.editor.assets"
})
public class TestConfig {
    public TestConfig() {
        Lwjgl3NativesLoader.load();
        Gdx.gl = mock(GL20.class);
        Gdx.files = new Lwjgl3Files();

        var graphics = mock(Graphics.class);
        Gdx.graphics = graphics;

        when(graphics.getWidth()).thenReturn(1024);
        when(graphics.getHeight()).thenReturn(768);
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

    @Bean
    public Registry registry() {
        return new Registry();
    }


    @Bean
    public MetaService metaService() {
        return new MetaService();
    }

    @Bean
    public TextureService textureService() {
        return new TextureService();
    }

    @Bean
    public ModelService modelService() {
        return new ModelService();
    }

    @Bean
    public TerrainService terrainService() {
        return new TerrainService();
    }

    @Bean
    public MaterialService materialService(MetaService metaService) {
        return new MaterialService(metaService);
    }

    @Bean
    public PixmapTextureService pixmapTextureService() {
        return new PixmapTextureService();
    }

    @Bean
    public ModelImporter modelImporter() {
        return new ModelImporter();
    }

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
}
