package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.loader.assimp.AssimpLoader;
import com.mbrlabs.mundus.commons.loader.G3dModelLoader;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
    @Bean
    public TextureAssetLoader textureService() {
        return new TextureAssetLoader();
    }

    @Bean
    public ModelAssetLoader modelService() {
        return new ModelAssetLoader();
    }

    @Bean
    public TerrainAssetLoader terrainAssetLoader() {
        return new TerrainAssetLoader();
    }

    @Bean
    public MaterialAssetLoader materialService() {
        return new MaterialAssetLoader();
    }

    @Bean
    public PixmapTextureAssetLoader pixmapTextureService() {
        return new PixmapTextureAssetLoader();
    }

    @Bean
    public SkyboxAssetLoader skyboxAssetLoader() {
        return new SkyboxAssetLoader();
    }

    @Bean
    public ShaderAssetLoader shaderAssetLoader() {
        return new ShaderAssetLoader();
    }

    @Bean
    public AssimpLoader assimpModelLoader() {
        return new AssimpLoader();
    }

    @Bean
    public G3dModelLoader g3dModelLoader() {
        return new G3dModelLoader(new JsonReader());
    }

    @Bean
    public G3dModelLoader g3dbModelLoader() {
        return new G3dModelLoader(new UBJsonReader());
    }

    @Bean
    public ModelImporter modelImporter() {
        return new ModelImporter(assimpModelLoader(), g3dModelLoader(), g3dbModelLoader());
    }


}
