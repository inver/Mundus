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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.tools.ToolHandle;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Marcus Brummer
 * @version 07-03-2016
 */
@Component
@Slf4j
public class ToolHandlePicker extends BasePicker {
    private final EditorCtx ctx;
    private final ModelBatch batch;

    public ToolHandlePicker(EditorCtx ctx, ShaderStorage shaderStorage) {
        this.ctx = ctx;
        batch = new ModelBatch(shaderStorage);
    }

    public ToolHandle pick(List<ToolHandle> handles, int screenX, int screenY) {
        begin(ctx.getViewport());
        renderPickableScene(handles);
        end();

        var pm = getFrameBufferPixmap(ctx.getViewport());

//        PixmapIO.writePNG(new FileHandle(
//                        "/Users/inv3r/Development/gamedev/Mundus/projects/app-editor/src/main/com/" +
//                                "mbrlabs/mundus/editor/tools/picker/tool_handle_image.png"),
//                pm);

        int x = HdpiUtils.toBackBufferX(screenX - ctx.getViewport().getScreenX());
        int y = HdpiUtils.toBackBufferY(screenY -
                (Gdx.graphics.getHeight() - (ctx.getViewport().getScreenY() + ctx.getViewport().getScreenHeight())));

        int id = new Color(pm.getPixel(x, y)).toIntBits();
        pm.dispose();

        log.debug("Picking handle with id {}", id);
        for (var handle : handles) {
            if (handle.getId() == id) {
                return handle;
            }
        }

        return null;
    }

    private void renderPickableScene(List<ToolHandle> handles) {
        batch.begin(ctx.getCurrent().getCamera());
        for (ToolHandle handle : handles) {
            handle.render(batch, environmentPool.obtain(), 0);
        }
        batch.end();
    }
}
