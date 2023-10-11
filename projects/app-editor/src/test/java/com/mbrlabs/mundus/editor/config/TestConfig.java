package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.core.ecs.EcsConfigurator;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.commons.model.ModelService;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import com.mbrlabs.mundus.editor.config.ui.TestOutline;
import com.mbrlabs.mundus.editor.core.project.ProjectStorage;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import com.mbrlabs.mundus.editor.ui.modules.outline.Outline;
import com.mbrlabs.mundus.editor.ui.modules.outline.OutlinePresenter;
import com.mbrlabs.mundus.editor.ui.widgets.ButtonFactory;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.ModelBatch;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ComponentScan({
        "com.mbrlabs.mundus.editor.core",
        "com.mbrlabs.mundus.editor.events",
        "com.mbrlabs.mundus.editor.assets",
        "com.mbrlabs.mundus.editor.ui",
        "com.mbrlabs.mundus.editor.utils",
        "com.mbrlabs.mundus.editor.tools",
        "com.mbrlabs.mundus.editor.input",
        "com.mbrlabs.mundus.editor.history",
})
@RequiredArgsConstructor
public class TestConfig {

    @Bean
    @Primary
    public AppEnvironment appEnvironment() {
        var homeDir = "/tmp/" + UUID.randomUUID() + "_random_home";
        new File(homeDir).mkdirs();
        return new AppEnvironment() {
            @Override
            public String getHomeDir() {
                return homeDir;
            }
        };
    }

    @Bean
    @Primary
    public Outline outline(OutlinePresenter outlinePresenter) {
        return new TestOutline(appUi(), outlinePresenter);
    }

    @Bean
    public SceneConverter sceneConverter(ObjectMapper mapper) {
        return new SceneConverter(mapper);
    }

    @Bean
    public MetaService metaService(ObjectMapper mapper) {
        return new MetaService(mapper);
    }

    @Bean
    public EventBus eventBus() {
        return Mockito.spy(new EventBus());
    }

    @Bean
    @Primary
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
    public UiComponentHolder uiComponentHolder() {
        var buttonMock = mock(VisTextButton.class);
        when(buttonMock.getStyle()).thenReturn(mock(TextButton.TextButtonStyle.class));
        when(buttonMock.getLabel()).thenReturn(mock(Label.class));

        var buttonFactoryMock = mock(ButtonFactory.class);
        when(buttonFactoryMock.createButton(anyString())).thenReturn(buttonMock);

        var res = mock(UiComponentHolder.class);
        when(res.getButtonFactory()).thenReturn(buttonFactoryMock);
        return res;
    }

    @Bean
    public Registry registry(ProjectStorage projectStorage) {
        return projectStorage.loadRegistry();
    }

    @Bean
    public EcsConfigurator ecsConfigurator(AssetManager assetManager, TerrainService terrainService,
                                           ModelService modelService) {
        return new EcsConfigurator(assetManager, terrainService, modelService);
    }
}
