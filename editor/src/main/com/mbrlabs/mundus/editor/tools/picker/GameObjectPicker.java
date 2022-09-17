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

package com.mbrlabs.mundus.editor.tools.picker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.scene3d.components.PickableComponent;
import lombok.RequiredArgsConstructor;

/**
 * Renders a scene graph to an offscreen FBO, encodes the game object's id in
 * the game object's render color (see GameObjectPickerShader) and does mouse
 * picking by decoding the picked color.
 * <p>
 * See also:
 * http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
 *
 * @author Marcus Brummer
 * @version 20-02-2016
 */
@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class GameObjectPicker extends BasePicker {
    private final EditorCtx ctx;
    private final ModelBatch batch;

    public GameObject pick(Scene scene, int screenX, int screenY) {
        begin(ctx.getViewport());
        renderPickableScene(scene);
        end();
        Pixmap pm = getFrameBufferPixmap(ctx.getViewport());

        int x = screenX - ctx.getViewport().getScreenX();
        int y = screenY - (Gdx.graphics.getHeight() - (ctx.getViewport().getScreenY() + ctx.getViewport().getScreenHeight()));

        int id = PickerColorEncoder.decode(pm.getPixel(x, y));
        for (GameObject go : scene.getSceneGraph().getGameObjects()) {
            if (id == go.getId()) return go;
            for (GameObject child : go) {
                if (id == child.getId()) return child;
            }
        }

        return null;
    }

    private void renderPickableScene(Scene scene) {
        batch.begin(ctx.getCamera());
        for (GameObject go : scene.getSceneGraph().getGameObjects()) {
            renderPickableGameObject(scene, go);
        }
        batch.end();
    }

    private void renderPickableGameObject(Scene scene, GameObject go) {
        for (Component c : go.getComponents()) {
            if (c instanceof PickableComponent) {
                c.render(batch, scene.getEnvironment(), Gdx.graphics.getDeltaTime());
            }
        }

        if (go.getChildren() != null) {
            for (GameObject goc : go.getChildren()) {
                renderPickableGameObject(scene, goc);
            }
        }
    }

}
