package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.core.ecs.EcsConfigurator;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.commons.model.ModelService;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import com.mbrlabs.mundus.editor.core.light.LightService;
import com.mbrlabs.mundus.editor.core.project.ProjectStorage;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.ui.ecs.RenderableDelegateSerializer;
import com.mbrlabs.mundus.editor.ui.ecs.camera.CameraBodyRenderDelegate;
import com.mbrlabs.mundus.editor.ui.ecs.camera.CameraService;
import com.mbrlabs.mundus.editor.ui.ecs.light.LightRenderDelegate;
import net.nevinsky.abyssus.core.ModelBatch;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

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

    @Bean
    public SceneConverter sceneConverter(ObjectMapper mapper) {
        return new SceneConverter(mapper);
    }

    @Bean
    public EcsConfigurator ecsConfigurator(AssetManager assetManager, TerrainService terrainService,
                                           ModelService modelService, CameraService cameraService,
                                           LightService lightService) {
        return new EcsConfigurator(assetManager, terrainService, modelService) {
            @Override
            protected List<Pair<Class<?>, JsonSerializer<?>>> getSerializers() {
                var serializer = new RenderableDelegateSerializer(cameraService, lightService);
                var res = new ArrayList<>(super.getSerializers());
                res.add(Pair.of(LightRenderDelegate.class, serializer));
                res.add(Pair.of(CameraBodyRenderDelegate.class, serializer));
                return res;
            }
        };
    }
}
