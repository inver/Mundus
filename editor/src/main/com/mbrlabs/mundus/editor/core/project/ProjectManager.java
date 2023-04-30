/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.core.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.core.ecs.EcsService;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.LogEvent;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.DEFAULT_SKYBOX_NAME;
import static com.mbrlabs.mundus.editor.core.ProjectConstants.PROJECT_ASSETS_DIR;
import static com.mbrlabs.mundus.editor.core.ProjectConstants.PROJECT_SCENES_DIR;

/**
 * Manages Mundus projects and scenes.
 *
 * @author Marcus Brummer
 * @version 25-11-2015
 */
@org.springframework.stereotype.Component
@Slf4j
@RequiredArgsConstructor
public class ProjectManager implements Disposable {

    private final EditorCtx editorCtx;
    private final Registry registry;
    private final ProjectStorage projectStorage;
    private final EditorAssetManager assetManager;
    private final SceneConverter sceneConverter;
    private final EventBus eventBus;
    private final SceneStorage sceneStorage;
    private final EcsService ecsService;

    /**
     * Saves the active project
     */
    public void saveCurrentProject() {
        saveProject(editorCtx.getCurrent());
    }

    /**
     * Creates & saves a new project.
     * <p>
     * Creates a new project. However, it does not switch the current project.
     *
     * @param name   project name
     * @param folder absolute path to project folder
     * @return new project context
     */
    public ProjectContext createProject(String name, String folder) {
        var ref = registry.createProjectRef(name, folder);
        var path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_ASSETS_DIR).mkdirs();
        new File(path, PROJECT_SCENES_DIR).mkdirs();

        var ctx = new ProjectContext(-1, new PerspectiveCamera());
        ctx.path = path;
        ctx.name = ref.getName();

        var scene = sceneStorage.createDefault(ctx.path, ctx.obtainID());
        scene.getEnvironment().setSkyboxName(DEFAULT_SKYBOX_NAME);
        sceneStorage.copyAssetToProject(
                ctx.path, editorCtx.getAssetLibrary().get(ProjectConstants.DEFAULT_SKYBOX_PATH)
        );
        loadSkybox(ctx, scene);

        ctx.setCurrentScene(scene);
        saveProject(ctx);


        var newCtx = loadProject(ref);
//        ctx.getCurrentScene().setEnvironment(newCtx.getCurrentScene().getEnvironment());
//        ctx.getCurrentScene().getCameras().clear();
//        ctx.getCurrentScene().getCameras().addAll(newCtx.getCurrentScene().getCameras());
        return ctx;
    }

    private void loadSkybox(ProjectContext ctx, Scene scene) {
        scene.getAssets().add(assetManager.loadProjectAsset(ctx.path, scene.getEnvironment().getSkyboxName()));
    }

    /**
     * Imports (opens) a mundus project, that is not in the registry.
     *
     * @param absolutePath path to project
     * @return project context of imported project
     * @throws ProjectAlreadyImportedException if project exists already in registry
     * @throws ProjectOpenException            project could not be opened
     */
    public ProjectContext importProject(String absolutePath)
            throws ProjectAlreadyImportedException, ProjectOpenException {
        // check if already imported
        for (ProjectRef ref : registry.getProjects()) {
            if (ref.getPath().equals(absolutePath)) {
                throw new ProjectAlreadyImportedException("Project " + absolutePath + " is already imported");
            }
        }

        ProjectRef ref = new ProjectRef();
        ref.setPath(absolutePath);

        try {
            ProjectContext context = loadProject(ref);
            ref.setName(context.name);
            registry.getProjects().add(ref);
            projectStorage.saveRegistry(registry);
            return context;
        } catch (Exception e) {
            throw new ProjectOpenException(e.getMessage());
        }
    }

    /**
     * Loads the project context for a project.
     * <p>
     * This does not open to that project, it only loads it.
     *
     * @param ref project reference to the project
     * @return loaded project context
     * @throws FileNotFoundException if project can't be found
     */
    public ProjectContext loadProject(ProjectRef ref) {
        var context = projectStorage.loadProjectContext(ref);
        if (context == null) {
            // project doesn't exist, but ref is exist - create default project
            return createProject(ref.getName(), ref.getPath());
        }
        editorCtx.setCurrent(context);

        context.setCurrentScene(loadScene(context, context.getActiveSceneName()));

        return context;
    }

    /**
     * Completely saves a project & all scenes.
     *
     * @param projectContext project context
     */
    public void saveProject(ProjectContext projectContext) {
        // save modified assets
//        var assetManager = projectContext.getAssetManager();
//        assetManager.getDirtyAssets().forEach(asset -> {
//            try {
//                log.debug("Saving dirty asset: {}", asset);
//                assetManager.saveAsset(asset);
//            } catch (Exception e) {
//                log.error("ERROR", e);
//            }
//        });
//        assetManager.getDirtyAssets().clear();

        // save current in .pro file
        projectStorage.saveProjectContext(projectContext);
        // save scene in .mundus file
        sceneStorage.saveScene(projectContext.path, projectContext.getCurrentScene());

        log.debug("Saving currentProject {}", projectContext.name + " [" + projectContext.path + "]");
        eventBus.post(new LogEvent("Saving currentProject " + projectContext.name + " [" + projectContext.path + "]"));
    }

    /**
     * Loads the project that was open when the user quit the program.
     * <p>
     * Does not open open the project.
     *
     * @return project context of last project
     */
    public ProjectContext loadLastProject() {
        ProjectRef lastOpenedProject = registry.getLastProject();
        if (lastOpenedProject != null) {
            try {
                return loadProject(lastOpenedProject);
            } catch (Exception e) {
                log.error("ERROR", e);
            }
            return null;
        }

        return null;
    }

    /**
     * Opens a project.
     * <p>
     * Opens a project. If a project is already open it will be disposed.
     *
     * @param context project context to open
     */
    public void changeProject(ProjectContext context) {
        if (editorCtx.getCurrent() != null) {
            editorCtx.getCurrent().dispose();
        }

        editorCtx.setCurrent(context);

        // currentProject.copyFrom(context);
        registry.setLastProject(new ProjectRef());
        registry.getLastProject().setName(context.name);
        registry.getLastProject().setPath(context.path);

        projectStorage.saveRegistry(registry);

        Gdx.graphics.setTitle(constructWindowTitle());
        eventBus.post(new ProjectChangedEvent(context));
    }

    /**
     * Creates a new scene for the given project.
     *
     * @param project project
     * @param name    scene name
     * @return newly created scene
     */
//    public Scene createScene(ProjectContext project, String name) {
//        Scene scene = new Scene();
//        long id = project.obtainID();
//        scene.setId(id);
//        scene.setName(name);
//        scene.skybox = SkyboxBuilder.createDefaultSkybox();
//        project.getScenes().add(scene.getName());
//        sceneStorage.saveScene(project.path, scene);
//
//        return scene;
//    }

    /**
     * Loads a scene.
     * <p>
     * This does not open the scene.
     *
     * @param context   project context of the scene
     * @param sceneName name of the scene
     * @return loaded scene
     * @throws FileNotFoundException if scene file not found
     */
    public Scene loadScene(ProjectContext context, String sceneName) {
        var dto = sceneStorage.loadScene(context.path, sceneName);

        var scene = new Scene(ecsService.createWorld());

        //todo preload assets to cache
        sceneConverter.fillScene(scene, dto);
        loadSkybox(context, scene);

        return scene;
    }

    @SneakyThrows
    /**
     * Loads and opens scene
     *
     * @param projectContext project context of scene
     * @param sceneName      scene name
     */
    public void changeScene(ProjectContext projectContext, String sceneName) {
//        try {
        Scene newScene = loadScene(projectContext, sceneName);
        projectContext.getCurrentScene().dispose();
        projectContext.setCurrentScene(newScene);

        Gdx.graphics.setTitle(constructWindowTitle());
        eventBus.post(new SceneChangedEvent());
//        } catch (FileNotFoundException e) {
//            log.error("ERROR, e");
//        }
    }

    private String constructWindowTitle() {
        return editorCtx.getCurrent().name + " - " + editorCtx.getCurrent().getCurrentScene().getName() +
                " [" + editorCtx.getCurrent().path + "]" + " - " + AppUtils.getAppVersion();
    }

    @Override
    public void dispose() {
        editorCtx.dispose();
    }

    public ProjectContext createDefaultProject() {
        if (registry.getLastProject() == null || registry.getProjects().size() == 0) {
            var path = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), "MundusProjects");

            return createProject("Default Project", path);
        }

        return null;
    }
}
