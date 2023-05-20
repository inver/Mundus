package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.importer.CameraDeserializer;
import com.mbrlabs.mundus.commons.importer.CameraSerializer;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.editor.core.assets.AssetWriter;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.assets.EditorModelService;
import com.mbrlabs.mundus.editor.core.assets.EditorTerrainService;
import com.mbrlabs.mundus.editor.core.ecs.EditorEcsService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import com.mbrlabs.mundus.editor.core.shader.EditorShaderHolder;
import com.mbrlabs.mundus.editor.core.shader.ShaderClassLoader;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import com.mbrlabs.mundus.editor.ui.components.camera.CameraService;
import com.mbrlabs.mundus.editor.ui.widgets.ButtonFactory;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderHolder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.util.UUID;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS;
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
        "com.mbrlabs.mundus.editor.core.assets",
})
@Import({
        UiConfig.class,
})
public class TestConfig {

    @Autowired
    private CameraService cameraService;

    @Bean
    public AppEnvironment appEnvironment() {
        var homeDir = "/tmp/" + UUID.randomUUID() + ".mundus";
        new File(homeDir).mkdirs();
        return new AppEnvironment() {
            @Override
            public String getHomeDir() {
                return homeDir;
            }
        };
    }

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
    public EditorCtx editorCtx() {
        return new EditorCtx();
    }


    @Bean
    public TextureAssetLoader textureService() {
        return new TextureAssetLoader();
    }

    @Autowired
    private AssetWriter assetWriter;
    @Autowired
    private EditorAssetManager editorAssetManager;
    @Autowired
    private EditorTerrainService terrainService;
    @Autowired
    private EditorModelService modelService;

    @Bean
    public ShaderAssetLoader shaderAssetLoader() {
        return new ShaderAssetLoader();
    }

    @Bean
    public AssetsStorage assetsStorage() {
        return new AssetsStorage(assetWriter, shaderAssetLoader(), editorCtx());
    }

    @Bean
    public SceneConverter sceneConverter() {
        return new SceneConverter(mapper());
    }

    @Bean
    public EditorEcsService ecsService() {
        return new EditorEcsService(editorAssetManager, terrainService, modelService);
    }

    @Bean
    public SceneStorage sceneStorage() {
        return new SceneStorage(mapper(), assetsStorage(), editorAssetManager, sceneConverter(), ecsService());
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

//    @Bean
//    public ShaderStorage shaderStorage() {
//        return new ShaderStorage(editorCtx(), eventBus(), shaderAssetLoader());
//    }

    @Bean
    public ShaderClassLoader shaderClassLoader() {
        return new ShaderClassLoader() {
            @Override
            public EditorShaderHolder reloadShader(ShaderAsset asset, ShaderHolder holder) {
                return mock(EditorShaderHolder.class);
            }
        };
    }

    @Bean
    public UiComponentHolder uiComponentHolder() {
        var buttonMock = mock(VisTextButton.class);
        when(buttonMock.getStyle()).thenReturn(mock(TextButton.TextButtonStyle.class));
        when(buttonMock.getLabel()).thenReturn(mock(Label.class));

        var buttonFactoryMock = mock(ButtonFactory.class);
        when(buttonFactoryMock.createButton(any())).thenReturn(buttonMock);

        var res = mock(UiComponentHolder.class);
        when(res.getButtonFactory()).thenReturn(buttonFactoryMock);
        return res;
    }
}
