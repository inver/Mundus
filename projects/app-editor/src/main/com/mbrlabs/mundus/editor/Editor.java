package com.mbrlabs.mundus.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAsset;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.project.ProjectWatcher;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.events.CameraChangedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.input.DirectCameraController;
import com.mbrlabs.mundus.editor.input.FreeCamController;
import com.mbrlabs.mundus.editor.input.InputService;
import com.mbrlabs.mundus.editor.input.ShortcutController;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.ecs.CoordinateSystemComponent;
import com.mbrlabs.mundus.editor.ui.modules.StatusBar;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.ExitDialog;
import com.mbrlabs.mundus.editor.ui.modules.dock.DockBar;
import com.mbrlabs.mundus.editor.ui.modules.dock.DockBarPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.Inspector;
import com.mbrlabs.mundus.editor.ui.modules.outline.Outline;
import com.mbrlabs.mundus.editor.ui.modules.toolbar.AppToolbar;
import com.mbrlabs.mundus.editor.ui.widgets.MundusMultiSplitPane;
import com.mbrlabs.mundus.editor.ui.widgets.MundusSplitPane;
import com.mbrlabs.mundus.editor.utils.Compass;
import com.mbrlabs.mundus.editor.utils.GlUtils;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Editor implements ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener,
        CameraChangedEvent.CameraChangedListener {

    private final EditorCtx ctx;
    private final FreeCamController camController;
    private final DirectCameraController directCameraController;
    private final ShortcutController shortcutController;
    private final InputService inputService;
    private final ProjectManager projectManager;
    private final ToolManager toolManager;
    private final ModelBatch batch;
    private final EventBus eventBus;
    private final AppUi appUi;
    private final ExitDialog exitDialog;
    private final ShaderStorage shaderStorage;
    private final DockBarPresenter dockBarPresenter;
    private final Outline outline;
    private final AppToolbar toolbar;
    private final StatusBar statusBar;
    private final Inspector inspector;
    private final ProjectWatcher projectWatcher;

    private DockBar dockBar;
    private Compass compass;

    private CoordinateSystemComponent wirePlane;

    public void create() {
        // init stuff
        setupUI();

        eventBus.register(this);

        setupInput();

        // TODO dispose this
        var axes = UsefulMeshs.createAxes();
//        ModelInstance axesInstance = new ModelInstance(axes);

        // open last edited project or create default project
        var context = projectManager.loadLastProject();
        if (context == null) {
            context = projectManager.createDefaultProject();
        }

        if (context == null) {
            throw new GdxRuntimeException("Couldn't open a project");
        }

        compass = new Compass(null);
        // change project; this will fire a ProjectChangedEvent
        projectManager.changeProject(context);

        wirePlane = new CoordinateSystemComponent();
    }


    private void setupUI() {
        var root = new VisTable();
        appUi.addActor(root);
        root.setFillParent(true);

        var mainContainer = new VisTable();
        var splitPane = new MundusSplitPane(mainContainer, null, true);

        // row 1: toolbar
        root.add(toolbar.getRoot()).fillX().expandX().row();

        // row 2: sidebar & 3d viewport & inspector
        var multiSplit = new MundusMultiSplitPane(false);
        multiSplit.setDraggable(false);
        multiSplit.setWidgets(outline, appUi.getSceneWidget(), inspector);
        multiSplit.setSplit(0, 0.2f);
        multiSplit.setSplit(1, 0.8f);
        mainContainer.add(multiSplit).grow().row();

        root.add(splitPane).grow().row();

        // row 3: DOCKER
        dockBar = new DockBar(splitPane, dockBarPresenter);
        root.add(dockBar).bottom().expandX().fillX().height(30f).row();

        // row 4: status bar
        root.add(statusBar).expandX().fillX().height(25f).row();
    }

    private void setupInput() {
        // NOTE: order in which processors are added is important: first added,
        // first executed!
        inputService.addProcessor(shortcutController);
        inputService.addProcessor(appUi);
        // when user does not click on a ui element -> unfocus UI
        inputService.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                appUi.unfocusAll();
                return false;
            }
        });
        inputService.addProcessor(toolManager);
        inputService.addProcessor(directCameraController);
//        inputManager.addProcessor(camController);
        inputService.activateDefaultTool();
    }

    private void setupSceneWidget() {
        var scene = ctx.getCurrent().getCurrentScene();

        appUi.getSceneWidget().setCamera(ctx.getCurrent().getCamera());
        appUi.getSceneWidget().setRenderer(camera -> {
            try {
                if (scene.getEnvironment().isSkyboxEnabled()) {
                    var skybox = ctx.getCurrent().getProjectAssets()
                            .get(new AssetKey(AssetType.SKYBOX, scene.getEnvironment().getSkyboxName()));
                    if (skybox != null) {
                        try {
                            batch.begin(camera);
                            batch.render(((SkyboxAsset) skybox).getBoxInstance(), scene.getEnvironment(),
                                    "sky");
                        } catch (Exception e) {
                            log.error("ERROR", e);
                        } finally {
                            batch.end();
                        }
                    }
                }
                batch.begin(camera);
                scene.render(batch, scene.getEnvironment(), shaderStorage, Gdx.graphics.getDeltaTime());
                wirePlane.render(batch, scene.getEnvironment(), shaderStorage, Gdx.graphics.getDeltaTime());
                //todo check current camera

                batch.end();

                toolManager.render(batch, scene.getEnvironment(), shaderStorage, Gdx.graphics.getDeltaTime());
                compass.render(batch, ShaderProvider.DEFAULT_SHADER_KEY);
            } catch (Exception e) {
                log.error("ERROR", e);
            }
        });

        compass.setWorldCam(ctx.getCurrent().getCamera());
        directCameraController.setCurrent(ctx.getCurrent().getCamera());
//        camController.setCamera(ctx.getCamera());
        appUi.getSceneWidget().setCamera(ctx.getCurrent().getCamera());
        ctx.setViewport(appUi.getSceneWidget().getViewport());
    }

    public void render() {
        GlUtils.clearScreen(Color.WHITE);
        appUi.act();
        dockBar.update();
//        todo
//        directCameraController.update();
        camController.update();
        toolManager.act();
        appUi.draw();
    }

    public void resize(int width, int height) {
        appUi.getViewport().update(width, height, true);
    }

    public void dispose() {
        projectWatcher.dispose();
        shaderStorage.dispose();
    }

    @Override
    public void onProjectChanged(@NotNull ProjectChangedEvent event) {
        setupSceneWidget();
        compass.setWorldCam(ctx.getCurrent().getCamera());
    }

    @Override
    public void onSceneChanged(@NotNull SceneChangedEvent event) {
        setupSceneWidget();
    }

    public boolean closeRequested() {
        appUi.showDialog(exitDialog);
        return false;
    }

    @Override
    public void onCameraChanged(@NotNull CameraChangedEvent event) {
        setupSceneWidget();
    }
}
