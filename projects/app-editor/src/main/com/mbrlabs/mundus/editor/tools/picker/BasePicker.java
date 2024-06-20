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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @author Marcus Brummer
 * @version 07-03-2016
 */
@Slf4j
public abstract class BasePicker implements Disposable {

    protected FrameBuffer fbo;
    protected ByteBuffer pixelBuffer = null;
    protected byte[] tmpArr = null;
    protected final FlushablePool<SceneEnvironment> environmentPool = new FlushablePool<>() {
        @Override
        protected SceneEnvironment newObject() {
            return new SceneEnvironment();
        }

        @Override
        public SceneEnvironment obtain() {
            var res = super.obtain();
            res.clear();
            return res;
        }
    };

    public BasePicker() {
        int width = HdpiUtils.toBackBufferX(Gdx.graphics.getWidth());
        int height = HdpiUtils.toBackBufferY(Gdx.graphics.getHeight());

        try {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        } catch (Exception e) {
            log.error("FBO dimensions 100%", e);
            width *= 0.9f;
            height *= 0.9f;
            try {
                fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
            } catch (Exception ee) {
                log.error("FBO dimensions 90%", e);
            }
        }

    }

    protected void begin(Viewport viewport) {
        fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        HdpiUtils.glViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(),
                viewport.getScreenHeight());
    }

    protected void end() {
        fbo.end();
    }

    public Pixmap getFrameBufferPixmap(Viewport viewport) {
        int w = HdpiUtils.toBackBufferX(viewport.getScreenWidth());
        int h = HdpiUtils.toBackBufferY(viewport.getScreenHeight());
        int x = HdpiUtils.toBackBufferX(viewport.getScreenX());
        int y = HdpiUtils.toBackBufferY(viewport.getScreenY());

        var bufferSize = w * h * 4;
        if (pixelBuffer == null || pixelBuffer.capacity() != bufferSize) {
            pixelBuffer = BufferUtils.newByteBuffer(bufferSize);
            tmpArr = new byte[bufferSize];
        }

        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fbo.getFramebufferHandle());
        Gdx.gl.glReadPixels(x, y, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pixelBuffer);
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0);

        //todo rework without tmpArr array
        final int numBytesPerLine = w * 4;
        for (int i = 0; i < h; i++) {
            pixelBuffer.position((h - i - 1) * numBytesPerLine);
            pixelBuffer.get(tmpArr, i * numBytesPerLine, numBytesPerLine);
        }

        BufferUtils.copy(tmpArr, 0, pixmap.getPixels(), tmpArr.length);

        return pixmap;
    }

    @Override
    public void dispose() {
        fbo.dispose();
    }
}
