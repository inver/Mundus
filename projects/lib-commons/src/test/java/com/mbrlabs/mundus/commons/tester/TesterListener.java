package com.mbrlabs.mundus.commons.tester;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.mbrlabs.mundus.commons.loader.AssimpWorker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.shader.DefaultShaderProvider;
import net.nevinsky.abyssus.core.shader.ShaderConfig;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;

@Slf4j
public class TesterListener extends Lwjgl3WindowAdapter implements ApplicationListener {

    private Camera camera;
    private ModelBatch modelBatch;
    //    private ModelInstance cubeInstance;
    private ModelInstance modelInstance;
    public CameraInputController camController;

    private final Environment environment = new Environment();
    private final AssimpWorker loader = new AssimpWorker();

    @SneakyThrows
    @Override
    public void create() {
        loadModel();
        createCamera();
//        createCube();

        var shaderConfig = new ShaderConfig();
        shaderConfig.setVertexShader(
                IOUtils.resourceToString("shader/default.vert.glsl", Charset.defaultCharset(), this.getClass()
                        .getClassLoader()));
        shaderConfig.setFragmentShader(
                IOUtils.resourceToString("shader/default.frag.glsl", Charset.defaultCharset(), this.getClass()
                        .getClassLoader()));

        modelBatch = new ModelBatch(new DefaultShaderProvider(shaderConfig));

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
    }

    private void createCamera() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(5f, 5f, 5f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
    }

    private void createCube() {
        var modelBuilder = new ModelBuilder();
        var model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(model);
    }

    private void loadModel() {
        var ts = System.currentTimeMillis();
        var model = loader.loadModel(new FileHandle(
                "/home/inv3r/Development/gamedev/Mundus/projects/lib-commons/src/test/resources/obj" +
                        "/piper/piper_pa18.obj"
        ));
        log.info("Model created in {} ms", System.currentTimeMillis() - ts);
        ts = System.currentTimeMillis();
        modelInstance = new ModelInstance(model);
        log.info("Model instance created in {} ms", System.currentTimeMillis() - ts);
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
        modelBatch.render(modelInstance, environment, ShaderProvider.DEFAULT_SHADER_KEY);
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
