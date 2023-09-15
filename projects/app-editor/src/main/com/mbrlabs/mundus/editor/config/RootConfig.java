package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.commons.loader.AssimpWorker;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import com.mbrlabs.mundus.editor.core.ecs.EditorEcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectStorage;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.ui.components.camera.CameraService;
import net.nevinsky.abyssus.core.ModelBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "com.mbrlabs.mundus.editor"
})
public class RootConfig {

    @Bean
    public Registry registry(ProjectStorage projectStorage) {
        return projectStorage.loadRegistry();
    }

    @Bean
    public ShapeRenderer shapeRenderer() {
        return new ShapeRenderer();
    }

    @Bean
    public ModelBatch modelBatch(ShaderStorage shaderStorage) {
        return new ModelBatch(shaderStorage);
    }

    @Bean
    public MetaService metaService(ObjectMapper mapper) {
        return new MetaService(mapper);
    }


//    @Bean
//    public CameraService cameraService() {
//        return new CameraService(editorCtx(), ecsService);
//    }

    @Bean
    public SceneConverter sceneConverter(ObjectMapper mapper) {
        return new SceneConverter(mapper);
    }
}
