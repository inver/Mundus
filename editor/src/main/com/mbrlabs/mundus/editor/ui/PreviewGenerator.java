package com.mbrlabs.mundus.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisImage;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.env.AppEnvironment;
import com.mbrlabs.mundus.commons.env.lights.SunLight;
import com.mbrlabs.mundus.editor.shader.MaterialPreviewShader;
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget;
import org.springframework.stereotype.Component;

@Component
public class PreviewGenerator {

    private final float previewWidth = 80;
    private final float previewHeight = 80;

    private final MaterialPreviewShader shader;

    public PreviewGenerator() {
        shader = new MaterialPreviewShader(
                "bundled/shaders/material_preview.vert.glsl",
                "bundled/shaders/material_preview.frag.glsl"
        );
        shader.init();
    }

    public Actor generate(Asset asset) {
        if (asset instanceof TextureAsset) {
            return createImage(((TextureAsset) asset).getTexture());
        }

        if (asset instanceof MaterialAsset) {
            var texture = renderPreview((MaterialAsset) asset, (modelBatch, camera, instance, environment) -> {
                var fb = new FrameBuffer(Pixmap.Format.RGB888, Float.valueOf(previewWidth).intValue(),
                        Float.valueOf(previewHeight).intValue(), false);
                fb.begin();

                Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
                modelBatch.begin(camera);
                modelBatch.render(instance, environment, shader);
                modelBatch.end();

                fb.end();
                return fb.getColorBufferTexture();
            }, previewHeight, previewWidth);
            return createImage(texture);
        }

        // todo return empty preview texture
        return null;
    }

    private VisImage createImage(Texture texture) {
        var preview = new VisImage(texture);
        preview.setHeight(previewHeight);
        preview.setWidth(previewWidth);
        return preview;
    }

    private <T> T renderPreview(MaterialAsset materialAsset, PreviewCreator<T> previewCreator, float height, float width) {
        var sphereInstance = new ModelInstance(new ModelBuilder().createSphere(
                1.2f, 1.2f, 1.2f, 20, 20,
                materialAsset.applyToMaterial(new Material()), VertexAttributes.Usage.Position
        ));

        var config = new DefaultShader.Config();
        config.numBones = 60;

        var modelBatch = new ModelBatch(new DefaultShaderProvider(config));


        var sunLight = new SunLight();
        sunLight.position.set(new Vector3(-1, -1, -1));

        var environment = new AppEnvironment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        environment.add(sunLight);

        var camera = new PerspectiveCamera(67, width, height);
        camera.position.set(0, 0, 1);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        return previewCreator.create(modelBatch, camera, sphereInstance, environment);
    }

    public RenderWidget createPreviewWidget(AppUi appUi, MaterialAsset asset) {
        return renderPreview(asset, (modelBatch, camera, instance, environment) -> {
            var widget = new RenderWidget(appUi, camera);
            widget.setRenderer(cam -> {
                Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
                modelBatch.begin(cam);
                modelBatch.render(instance, environment, shader);
                modelBatch.end();
            });
            return widget;
        }, 250, 250);
    }

    private interface PreviewCreator<T> {
        T create(ModelBatch modelBatch, PerspectiveCamera camera, ModelInstance instance, Environment environment);
    }
}
