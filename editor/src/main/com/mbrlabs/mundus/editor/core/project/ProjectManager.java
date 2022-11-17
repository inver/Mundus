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
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.editor.Main;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.LogEvent;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.*;

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
    private final MetaService metaService;
    private final TextureAssetLoader textureService;
    private final TerrainAssetLoader terrainService;
    private final MaterialAssetLoader materialService;
    private final PixmapTextureAssetLoader pixmapTextureService;
    private final ModelAssetLoader modelService;

    private final EventBus eventBus;
    private final SceneStorage sceneStorage;
    private final ShaderStorage shaderStorage;
    private final AssetsStorage assetsStorage;

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

        var ctx = new ProjectContext(-1);
        ctx.path = path;
        ctx.name = ref.getName();

        var scene = sceneStorage.createDefault(ctx.path, ctx.obtainID());
        scene.getEnvironment().setSkyboxName(DEFAULT_SKYBOX_NAME);
        sceneStorage.copyAssetToProject(ctx.path,
                editorCtx.getAssetLibrary().get(ProjectConstants.DEFAULT_SKYBOX_PATH));
        loadSkybox(ctx, scene);

        ctx.setCurrentScene(scene);
        saveProject(ctx);


        var newCtx = loadProject(ref);
//        ctx.getCurrentScene().setEnvironment(newCtx.getCurrentScene().getEnvironment());
//        ctx.getCurrentScene().getCameras().clear();
//        ctx.getCurrentScene().getCameras().addAll(newCtx.getCurrentScene().getCameras());
        log.info("contexts equals: " + ctx.equals(newCtx));
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

        var scene = new Scene();

        //todo preload assets to cache
        SceneConverter.fillScene(scene, dto, editorCtx.getAssetLibrary());
        loadSkybox(context, scene);

        var sceneGraph = scene.getSceneGraph();
        for (GameObject go : sceneGraph.getGameObjects()) {
            initGameObject(context, go);
        }

        // create TerrainGroup for active scene
        List<Component> terrainComponents = new ArrayList<>();
        for (GameObject go : sceneGraph.getGameObjects()) {
            go.findComponentsByType(terrainComponents, Component.Type.TERRAIN, true);
        }
//        for (Component c : terrainComponents) {
//            if (c instanceof TerrainComponent) {
//                scene.terrains.add(((TerrainComponent) c).getTerrain());
//            }
//        }

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

    private void initGameObject(ProjectContext context, GameObject root) {
        initComponents(context, root);
        if (root.getChildren() != null) {
            for (GameObject c : root.getChildren()) {
                initGameObject(context, c);
            }
        }
    }

    private void initComponents(ProjectContext context, GameObject go) {
//        List<Asset> models = assetManager.getAssetsByType(AssetType.MODEL);
//        var iterator = go.getComponents().iterator();
//        while (iterator.hasNext()) {
//            var c = iterator.next();
//            if (c == null) {
//                // To prevent crashing, log a warning statement and remove the corrupted component
//                iterator.remove();
//                log.warn("A component for {} was null on load, this may be caused by deleting an asset that is still in " +
//                        "a scene.", go);
//                go.name = go.name + " [COMPONENT ERROR]";
//                continue;
//            }
//            // Model component
//            if (c.getType() == Component.Type.MODEL) {
//                ModelComponent modelComponent = (ModelComponent) c;
//                var modelOpt = models.stream()
//                        .filter(m -> m.getID().equals(modelComponent.getModelAsset().getID()))
//                        .findFirst();
//                if (modelOpt.isPresent()) {
//                    modelComponent.setModel((ModelAsset) modelOpt.get(), false);
//                } else {
//                    log.error("ERROR", "model for modelInstance not found: {}", modelComponent.getModelAsset().getID());
//                }
//            } else if (c.getType() == Component.Type.TERRAIN) {
//                ((TerrainComponent) c).getTerrain().getTerrain().setTransform(go.getTransform());
//            }
//
//            // encode id for picking
//            if (c instanceof PickableComponent) {
//                ((PickableComponent) c).encodeRayPickColorId();
//            }
//        }
    }

    private String constructWindowTitle() {
        return editorCtx.getCurrent().name + " - " + editorCtx.getCurrent().getCurrentScene().getName() +
                " [" + editorCtx.getCurrent().path + "]" + " - " + Main.TITLE;
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
