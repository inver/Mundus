package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import net.nevinsky.abyssus.core.AnimationController;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;

public class ModelTest extends Lwjgl3WindowAdapter implements ApplicationListener, InputProcessor {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private ModelInstance modelInstance;
    private Environment environment;
    private AnimationController controller;
    private boolean screenShot = false;
    private FrameBuffer frameBuffer;
    private Texture texture = null;
    private TextureRegion textureRegion;
    private SpriteBatch spriteBatch;

    public static void main(String[] args) {
        var config = new Lwjgl3ApplicationConfiguration();
        var listener = new ModelTest();
        config.setWindowListener(listener);
        new Lwjgl3Application(listener, config);
    }

    @Override
    public void create() {
        // Create camera sized to screens width/height with Field of View of 75
//        degrees
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Move the camera 5 units back along the z-axis and look at the origin
        camera.position.set(0f, 0f, 7f);
        camera.lookAt(0f, 0f, 0f);
        // Near and Far (plane) represent the minimum and maximum ranges of the camera in, um, units
        // camera.near = 0.1f;           camera.far = 300.0f;
        // A ModelBatch is like a SpriteBatch, just for models.  Use it to batch up geometry for OpenGL
        modelBatch = new ModelBatch();
        var builder = new ModelBuilder();
        var sphere = builder.createSphere(1.2f, 1.2f, 1.2f, 20, 20,
                new Material(), VertexAttributes.Usage.Position);

        modelInstance = new ModelInstance(sphere);
        // move the model down a bit on the screen ( in a z-up world, down is -z ).
        modelInstance.transform.translate(0, 0, -2);
        // Finally we want some light, or we wont see our color.  The environment gets passed in during
        // the rendering process.  Create one, then create an Ambient ( non-positioned, non-directional ) light.
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        // You use an AnimationController to um, control animations.  Each control is tied to the model instance
        controller = new AnimationController(modelInstance);
        // Pick the current animation by name
//        controller.setAnimation("Bend", 1, new AnimationListener() {
//            @Override
//            public void onEnd(AnimationDesc animation) {
//                // this will be called when the current animation is done.
//                // queue up another animation called "balloon".
//                // Passing a negative to loop count loops forever.  1f for speed is normal speed.
//                controller.queue("Balloon", -1, 1f, null, 0f);
//            }
//
//            @Override
//            public void onLoop(AnimationDesc animation) {
//                // TODO Auto-generated method stub
//            }
//        });
        frameBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        Gdx.input.setInputProcessor(this);
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
//        model.dispose();
    }

    @Override
    public void render() {
        // You've seen all this before, just be sure to clear the GL_DEPTH_BUFFER_BIT when working in 3D
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        // When you change the camera details, you need to call update();
        // Also note, you need to call update() at least once.
        camera.update();
        // You need to call update on the animation controller so it will advance the animation.  Pass in frame delta
        controller.update(Gdx.graphics.getDeltaTime());
        // If the user requested a screenshot, we need to call begin on our framebuffer
        // This redirects output to the framebuffer instead of the screen.
        if (screenShot) {
            frameBuffer.begin();
        }
        // Like spriteBatch, just with models!  pass in the box Instance and the environment
        modelBatch.begin(camera);
        modelBatch.render(modelInstance, environment);
        modelBatch.end();
        // Now tell OpenGL that we are done sending graphics to the framebuffer
        if (screenShot) {
            frameBuffer.end();              // get the graphics rendered to the framebuffer as a texture
            texture = frameBuffer.getColorBufferTexture();
            // welcome to the wonderful world of different coordinate systems!
            // simply put, the framebuffer is upside down to normal textures, so we have to flip it
            // Use a TextureRegion to do so
            textureRegion = new TextureRegion(texture);
            // and.... FLIP!  V (vertical) only
            textureRegion.flip(false, true);
        }
        // In the case that we have a texture object to actually draw, we do so
        // using the old familiar SpriteBatch to do so.
        if (texture != null) {
            spriteBatch.begin();
            spriteBatch.draw(textureRegion, 0, 0);
            spriteBatch.end();
            screenShot = false;
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }


}