package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.importExport.AssimpExporter;
import com.mbrlabs.mundus.commons.importer.CameraDeserializer;
import com.mbrlabs.mundus.commons.importer.CameraSerializer;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.commons.loader.AssimpModelLoader;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import com.mbrlabs.mundus.editor.core.ecs.EditorEcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectStorage;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.ui.components.camera.CameraService;
import net.nevinsky.abyssus.core.ModelBatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS;

@Configuration
@ComponentScan({
        "com.mbrlabs.mundus.editor"
})
public class RootConfig {
    @Autowired
    private EditorEcsService ecsService;

    @Bean
    public ObjectMapper mapper() {
        var res = new ObjectMapper();
        res.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        res.configure(ALLOW_NON_NUMERIC_NUMBERS, true);

        var appModule = new SimpleModule();
        appModule.addSerializer(PerspectiveCamera.class, new CameraSerializer());
        appModule.addDeserializer(PerspectiveCamera.class, new CameraDeserializer());

        res.registerModule(appModule);
        return res;
    }

    @Bean
    public Registry registry(ProjectStorage projectStorage) {
        return projectStorage.loadRegistry();
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
        return new MetaService(mapper());
    }

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
    public AssimpModelLoader assimpModelLoader() {
        return new AssimpModelLoader();
    }

    @Bean
    public ModelImporter modelImporter() {
        return new ModelImporter(assimpModelLoader());
    }

    @Bean
    public ShaderAssetLoader shaderAssetLoader() {
        return new ShaderAssetLoader();
    }

    @Bean
    public EditorCtx editorCtx() {
        return new EditorCtx();
    }


    @Bean
    public CameraService cameraService() {
        return new CameraService(editorCtx(), ecsService);
    }

    @Bean
    public SceneConverter sceneConverter() {
        return new SceneConverter(mapper());
    }

    @Bean
    public AssimpExporter gltfExporter() {
        return new AssimpExporter();
//        return new GltfExporter(mapper(), new GltfMeshExporter(), new GltfMaterialExporter(), new GltfSkinExporter(),
//                new GltfAnimationExporter());
    }
}
