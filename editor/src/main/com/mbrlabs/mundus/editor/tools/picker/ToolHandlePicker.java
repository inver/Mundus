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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.tools.ToolHandle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Marcus Brummer
 * @version 07-03-2016
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ToolHandlePicker extends BasePicker {
    private final EditorCtx ctx;
    private final ModelBatch batch;
    private final ShaderStorage shaderStorage;

    public ToolHandle pick(ToolHandle[] handles, Scene scene, int screenX, int screenY) {
        begin(ctx.getViewport());
        renderPickableScene(handles, batch, ctx.getCamera());
        end();
        Pixmap pm = getFrameBufferPixmap(ctx.getViewport());

        int x = screenX - ctx.getViewport().getScreenX();
        int y = screenY - (Gdx.graphics.getHeight() - (ctx.getViewport().getScreenY() + ctx.getViewport().getScreenHeight()));

        int id = PickerColorEncoder.decode(pm.getPixel(x, y));
        log.trace("ToolHandlePicker | Picking handle with id {}", id);
        for (ToolHandle handle : handles) {
            if (handle.getId() == id) {
                return handle;
            }
        }

        return null;
    }

    private void renderPickableScene(ToolHandle[] handles, ModelBatch batch, Camera cam) {
        batch.begin(cam);
        for (ToolHandle handle : handles) {
            handle.renderPick(batch, shaderStorage);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        fbo.dispose();
    }

}
