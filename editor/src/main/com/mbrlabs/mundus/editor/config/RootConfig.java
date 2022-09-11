package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mbrlabs.mundus.commons.assets.material.MaterialService;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelService;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureService;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainService;
import com.mbrlabs.mundus.commons.assets.texture.TextureService;
import com.mbrlabs.mundus.commons.core.registry.Registry;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import com.mbrlabs.mundus.editor.core.kryo.KryoManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "com.mbrlabs.mundus.editor"
})
public class RootConfig {
    @Bean
    public Registry registry(KryoManager kryoManager) {
        return kryoManager.loadRegistry();
    }

    @Bean
    public ShapeRenderer shapeRenderer() {
        return new ShapeRenderer();
    }

    @Bean
    public ModelBatch modelBatch() {
        return new ModelBatch();
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
    public ModelImporter modelImporter(Registry registry) {
        return new ModelImporter(registry);
    }
}