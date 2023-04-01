package com.mbrlabs.mundus.editor.tools.picker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.ModelBatch;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class EntityPicker extends BasePicker {
    private final EditorCtx ctx;
    private final ModelBatch batch;
    private final ShaderStorage shaderStorage;

    public int pick(Scene scene, int screenX, int screenY) {
        begin(ctx.getViewport());
        //todo
//        renderPickableScene(scene);
        end();
        Pixmap pm = getFrameBufferPixmap(ctx.getViewport());

        int x = screenX - ctx.getViewport().getScreenX();
        int y = screenY -
                (Gdx.graphics.getHeight() - (ctx.getViewport().getScreenY() + ctx.getViewport().getScreenHeight()));

        int id = PickerColorEncoder.decode(pm.getPixel(x, y));
        try {
            if (scene.getWorld().getEntity(id) != null) {
                return id;
            }
        } catch (IndexOutOfBoundsException e) {
            //ignore
        }
        return -1;
    }

    private void renderPickableScene(Scene scene) {
        batch.begin(ctx.getCurrent().getCamera());
//        for (GameObject go : scene.getSceneGraph().getGameObjects()) {
//            renderPickableGameObject(scene, go);
//        }
        batch.end();
    }

    private void renderPickableGameObject(Scene scene, GameObject go) {
//        for (Component c : go.getComponents()) {
//            if (c instanceof PickableComponent) {
//                c.render(batch, scene.getEnvironment(), shaderStorage, Gdx.graphics.getDeltaTime());
//            }
//        }
//
//        if (go.getChildren() != null) {
//            for (GameObject goc : go.getChildren()) {
//                renderPickableGameObject(scene, goc);
//            }
//        }
    }
}
