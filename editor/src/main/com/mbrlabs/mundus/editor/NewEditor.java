package com.mbrlabs.mundus.editor;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.input.FreeCamController;
import com.mbrlabs.mundus.editor.input.InputManager;
import com.mbrlabs.mundus.editor.input.ShortcutController;
import com.mbrlabs.mundus.editor.shader.Shaders;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.StatusBar;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.ExitDialog;
import com.mbrlabs.mundus.editor.ui.modules.dock.DockBar;
import com.mbrlabs.mundus.editor.ui.modules.inspector.Inspector;
import com.mbrlabs.mundus.editor.ui.modules.menubar.MenuBarController;
import com.mbrlabs.mundus.editor.ui.modules.menubar.MundusMenuBar;
import com.mbrlabs.mundus.editor.ui.modules.outline.Outline;
import com.mbrlabs.mundus.editor.ui.modules.toolbar.MundusToolbar;
import com.mbrlabs.mundus.editor.ui.widgets.MundusMultiSplitPane;
import com.mbrlabs.mundus.editor.ui.widgets.MundusSplitPane;
import com.mbrlabs.mundus.editor.utils.Compass;
import com.mbrlabs.mundus.editor.utils.GlUtils;
import com.mbrlabs.mundus.editor.utils.UsefulMeshs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class NewEditor implements ProjectChangedEvent.ProjectChangedListener, SceneChangedEvent.SceneChangedListener {

    private final FreeCamController camController;
    private final ShortcutController shortcutController;
    private final InputManager inputManager;
    private final ProjectManager projectManager;
    private final Registry registry;
    private final ToolManager toolManager;
    private final ModelBatch batch;
    private final EventBus eventBus;
    private final AppUi appUi;
    private final ExitDialog exitDialog;

    private final MenuBarController menuBarController;

    private Compass compass;
    private MundusMenuBar menuBar;
    private final Outline outline;
    private final MundusToolbar toolbar;
    private final StatusBar statusBar;
    private final Inspector inspector;
    private DockBar dockBar;

    public void create() {
        var homeDir = new File(Registry.HOME_DIR);
        if (!homeDir.exists()) {
            homeDir.mkdirs();
        }

        // init stuff
        setupUI();

        eventBus.register(this);

        setupInput();

        // TODO dispose this
        var axes = UsefulMeshs.createAxes();
        ModelInstance axesInstance = new ModelInstance(axes);

        // open last edited project or create default project
        var context = projectManager.loadLastProject();
        if (context == null) {
            context = createDefaultProject();
        }

        if (context == null) {
            throw new GdxRuntimeException("Couldn't open a project");
        }

        compass = new Compass(context.currScene.cam);

        // change project; this will fire a ProjectChangedEvent
        projectManager.changeProject(context);
    }


    private void setupUI() {
        menuBar = new MundusMenuBar(registry, menuBarController);
//        toolbar = new MundusToolbar();
//        outline = new Outline();
//        this.toolbar = toolbar;
//        this.outline = outline;
//        this.inspector = inspector;

//        greenSeparatorStyle = new Separator.SeparatorStyle(VisUI.getSkin().getDrawable("mundus-separator-green"), 1);

        var root = new VisTable();
        appUi.addActor(root);
        root.setFillParent(true);

        var mainContainer = new VisTable();
        var splitPane = new MundusSplitPane(mainContainer, null, true);

        // row 1: add menu
        root.add(menuBar.getTable()).fillX().expandX().row();

        // row 2: toolbar
        root.add(toolbar.getRoot()).fillX().expandX().row();

        // row 3: sidebar & 3d viewport & inspector
        var multiSplit = new MundusMultiSplitPane(false);
        multiSplit.setDraggable(false);
        multiSplit.setWidgets(outline, appUi.getSceneWidget(), inspector);
        multiSplit.setSplit(0, 0.2f);
        multiSplit.setSplit(1, 0.8f);
        mainContainer.add(multiSplit).grow().row();

        root.add(splitPane).grow().row();

        // row 4: DOCKER
        dockBar = new DockBar(splitPane, projectManager);
        root.add(dockBar).bottom().expandX().fillX().height(30f).row();

        // row 5: status bar
        root.add(statusBar).expandX().fillX().height(25f).row();
    }

    private void setupInput() {
        // NOTE: order in wich processors are added is important: first added,
        // first executed!
        inputManager.addProcessor(shortcutController);
        inputManager.addProcessor(appUi);
        // when user does not click on a ui element -> unfocus UI
        inputManager.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                appUi.unfocusAll();
                return false;
            }
        });
        inputManager.addProcessor(toolManager);
        inputManager.addProcessor(camController);
        toolManager.setDefaultTool();
    }

    private void setupSceneWidget() {
        var context = projectManager.getCurrent();
        var scene = context.currScene;
        var sg = scene.sceneGraph;

        appUi.getSceneWidget().setCam(context.currScene.cam);
        appUi.getSceneWidget().setRenderer(cam -> {
            if (scene.skybox != null) {
                batch.begin(scene.cam);
                batch.render(scene.skybox.getSkyboxInstance(), scene.environment, Shaders.INSTANCE.getSkyboxShader());
                batch.end();
            }

            sg.update();
            scene.render();

            toolManager.render();
            compass.render(batch);
        });

        compass.setWorldCam(context.currScene.cam);
        camController.setCamera(context.currScene.cam);
        appUi.getSceneWidget().setCam(context.currScene.cam);
        context.currScene.viewport = appUi.getSceneWidget().getViewport();
    }

    public void render() {
        GlUtils.clearScreen(Color.WHITE);
        appUi.act();
        dockBar.update();
        camController.update();
        toolManager.act();
        appUi.draw();
    }

    public void resize(int width, int height) {
        appUi.getViewport().update(width, height, true);
    }

    public void dispose() {
        Mundus.INSTANCE.dispose();
    }

    @Override
    public void onProjectChanged(@NotNull ProjectChangedEvent event) {
        setupSceneWidget();
    }

    @Override
    public void onSceneChanged(@NotNull SceneChangedEvent event) {
        setupSceneWidget();
    }

    private ProjectContext createDefaultProject() {
        if (registry.getLastOpenedProject() == null || registry.getProjects().size() == 0) {
            var path = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), "MundusProjects");

            return projectManager.createProject("Default Project", path);
        }

        return null;
    }

    public boolean closeRequested() {
        appUi.showDialog(exitDialog);
        return false;
    }
}
