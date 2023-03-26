package com.mbrlabs.mundus.commons.tester;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.mbrlabs.mundus.commons.loader.AssimpModelLoader;
import net.nevinsky.mundus.core.ModelBatch;
import net.nevinsky.mundus.core.ModelInstance;
import net.nevinsky.mundus.core.model.Model;

public class TesterListener extends Lwjgl3WindowAdapter implements ApplicationListener {

    private Camera camera;
    private ModelBatch modelBatch;
    //    private ModelInstance cubeInstance;
    private ModelInstance modelInstance;
    public CameraInputController camController;

    private final Environment environment = new Environment();
    private final AssimpModelLoader loader = new AssimpModelLoader();

    @Override
    public void create() {
        loadModel();
        createCamera();
//        createCube();

        modelBatch = new ModelBatch();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
    }

    private void createCamera() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
    }

//    private void createCube() {
//        var modelBuilder = new ModelBuilder();
//        var model = modelBuilder.createBox(5f, 5f, 5f,
//                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//        cubeInstance = new ModelInstance(model);
//    }

    private void loadModel() {
        var res = loader.loadModel("ololo", new FileHandle("/Users/inv3r/Development/gamedev/Mundus/commons/src/test/resources/ac3d/sr22.ac"));
        modelInstance = new ModelInstance(new Model(res));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camController.update();

        modelBatch.begin(camera);
        modelBatch.render(modelInstance, environment);
//        modelBatch.render(cubeInstance, environment);
        modelBatch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
