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

package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mbrlabs.mundus.editor.ui.AppUi;

/**
 * @author Marcus Brummer
 * @version 27-01-2016
 */
public class RenderWidget extends Widget {

    private static final Vector2 TMP_VEC = new Vector2();

    private final ScreenViewport viewport;
    private Camera camera;

    private Renderer renderer;

    private final AppUi appUi;

    public RenderWidget(AppUi appUi, PerspectiveCamera cam) {
        super();
        this.camera = cam;
        this.appUi = appUi;
        viewport = new ScreenViewport(cam);
    }

    public RenderWidget(AppUi appUi) {
        super();
        this.appUi = appUi;
        this.viewport = new ScreenViewport();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        viewport.setCamera(camera);
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (renderer == null || camera == null) {
            return;
        }

        // render part of the ui & pause rest
        batch.end();

        TMP_VEC.set(getOriginX(), getOriginY());
        localToStageCoordinates(TMP_VEC);
        final int width = (int) getWidth();
        final int height = (int) getHeight();

        // apply widget viewport
        viewport.setScreenBounds((int) TMP_VEC.x, (int) TMP_VEC.y, width, height);
        viewport.setWorldSize(width * viewport.getUnitsPerPixel(), height * viewport.getUnitsPerPixel());
        viewport.apply();

        // render 3d scene
        renderer.render(camera);

        // re-apply stage viewport
        appUi.getViewport().apply();

        // proceed ui rendering
        batch.begin();
    }

    /**
     * Used to render the 3d scene within this widget.
     */
    public interface Renderer {
        void render(Camera camera);
    }

}
