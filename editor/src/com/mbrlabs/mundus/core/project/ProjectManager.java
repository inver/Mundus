package com.mbrlabs.mundus.core.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.Main;
import com.mbrlabs.mundus.core.ImportManager;
import com.mbrlabs.mundus.core.HomeManager;
import com.mbrlabs.mundus.core.Scene;
import com.mbrlabs.mundus.core.kryo.KryoManager;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.events.EventBus;
import com.mbrlabs.mundus.events.ReloadAllModelsEvent;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainIO;
import com.mbrlabs.mundus.utils.Callback;
import com.mbrlabs.mundus.utils.Log;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author Marcus Brummer
 * @version 25-11-2015
 */
public class ProjectManager {

    public static final String PROJECT_MODEL_DIR = "models/";
    public static final String PROJECT_SCENES_DIR = "scenes/";
    public static final String PROJECT_TERRAIN_DIR = "terrains/";

    private ProjectContext projectContext;
    private HomeManager homeManager;
    private KryoManager kryoManager;

    private EventBus eventBus;

    public ProjectManager(ProjectContext projectContext, KryoManager kryoManager, HomeManager homeManager, EventBus eventBus) {
        this.projectContext = projectContext;
        this.homeManager = homeManager;
        this.kryoManager = kryoManager;
        this.eventBus = eventBus;
    }

    public ProjectRef createProject(String name, String folder) {
        ProjectRef ref = homeManager.createProjectRef(name, folder);
        String path = ref.getPath();
        new File(path).mkdirs();
        new File(path, PROJECT_MODEL_DIR).mkdirs();
        new File(path, PROJECT_TERRAIN_DIR).mkdirs();
        new File(path, PROJECT_SCENES_DIR).mkdirs();
        return ref;
    }

    private ProjectContext loadProject(ProjectRef ref) {
        ProjectContext context = kryoManager.loadProjectContext(ref);
        context.path = ref.getPath();
        context.name = ref.getName();
        context.id = ref.getId();

        // load g3db models
        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
        for(MModel model : context.models) {
            String g3dbPath = model.g3dbPath;
            model.setModel(loader.loadModel(Gdx.files.absolute(g3dbPath)));
        }

        // load terrain .terra files
        for(Terrain terrain : context.terrains) {
            TerrainIO.importTerrain(terrain, terrain.terraPath);
        }

        return context;
    }

    public void loadProject(ProjectRef ref, Callback<ProjectContext> callback) {
        new Thread() {
            @Override
            public void run() {
                ProjectContext context = loadProject(ref);
                if(new File(context.path).exists()) {
                    Gdx.app.postRunnable(() -> callback.done(context));
                } else {
                    Gdx.app.postRunnable(() -> callback.error("Project " + context.path + " not found."));
                }
            }
        }.run(); // FIXME run() is intended because of openGL context...either remove thread or find a way to run it async
    }

    public void changeProject(ProjectContext context) {
        homeManager.homeDescriptor.lastProject = context.id;
        homeManager.save();
        projectContext.dispose();
        projectContext.copyFrom(context);
        projectContext.loaded = true;
        Gdx.graphics.setTitle(projectContext.name+" ["+projectContext.path+"]" + " - " + Main.TITLE);
        eventBus.post(new ReloadAllModelsEvent());
    }

    public MModel importG3dbModel(ImportManager.ImportedModel importedModel) {
        long id = projectContext.obtainUUID();

        // copy to project's model folder
        String folder = projectContext.path + "/" + ProjectManager.PROJECT_MODEL_DIR + id + "/";
        FileHandle finalG3db = Gdx.files.absolute(folder + importedModel.g3dbFile.nameWithoutExtension() + "-" + id + ".g3db");
        importedModel.g3dbFile.copyTo(finalG3db);
        importedModel.textureFile.copyTo(Gdx.files.absolute(folder));

        // load model
        G3dModelLoader loader = new G3dModelLoader(new UBJsonReader());
        Model model = loader.loadModel(finalG3db);

        // create persistable model
        MModel mModel = new MModel();
        mModel.setModel(model);
        mModel.name = finalG3db.name();
        mModel.id = id;
        mModel.g3dbPath = finalG3db.path();
        System.out.println(finalG3db);
        projectContext.models.add(mModel);

        // save whole project
        saveProject(projectContext);

        return mModel;
    }

    public void saveProject(ProjectContext projectContext) {
        // TODO save

        // save terrain data in .terra files
        for(Terrain terrain : projectContext.terrains) {
            String path = FilenameUtils.concat(projectContext.path, ProjectManager.PROJECT_TERRAIN_DIR);
            path += terrain.name + "-" + terrain.id + "." + TerrainIO.FILE_EXTENSION;
            terrain.terraPath = path;
            TerrainIO.exportBinary(terrain, path);
        }

        // save context in .pro file
        kryoManager.saveProjectContext(projectContext);

        Log.debug("Saving project " + projectContext.name+ " [" + projectContext.path + "]");
    }



    public Scene createScene(ProjectContext projectContext, String name) {
        Scene scene = new Scene();
        long id = projectContext.obtainUUID();
        scene.setId(id);
        scene.setName(name);

        String sceneDir = FilenameUtils.concat(projectContext.path, scene.getName());
        String terrainsDir = FilenameUtils.concat(sceneDir, PROJECT_TERRAIN_DIR);
        new File(sceneDir).mkdir();
        new File(terrainsDir).mkdir();

        projectContext.scenes.add(scene);

        return scene;
    }

    public void saveScene() {

    }


}
