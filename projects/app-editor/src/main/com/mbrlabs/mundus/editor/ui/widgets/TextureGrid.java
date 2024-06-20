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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.utils.TextureProvider;

import java.util.List;

/**
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public class TextureGrid<T extends TextureProvider> extends VisTable {

    private final GridGroup grid;
    private OnTextureClickedListener listener;

    public TextureGrid(int imgSize, int spacing) {
        super();
        grid = new GridGroup(imgSize, spacing);
        add(grid).expand().fill().row();
    }

    public TextureGrid(int imgSize, int spacing, List<T> textures) {
        this(imgSize, spacing);
        setTextures(textures);
    }

    public void setListener(OnTextureClickedListener listener) {
        this.listener = listener;
    }

    public void setTextures(List<T> textures) {
        grid.clearChildren();
        for (T tex : textures) {
            grid.addActor(new TextureItem<>(tex));
        }
    }

    public void addTexture(T texture) {
        grid.addActor(new TextureItem<>(texture));
    }

    public void removeTextures() {
        grid.clearChildren();
    }

    /**
     *
     */
    public interface OnTextureClickedListener {
        void onTextureSelected(TextureProvider textureProvider, boolean leftClick);
    }

    /**
     *
     */
    private class TextureItem<T extends TextureProvider> extends VisTable {
        public TextureItem(final T tex) {
            super();
            add(new VisImage(tex.getTexture()));

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    listener.onTextureSelected(tex, button == Input.Buttons.LEFT);
                }
            });

        }
    }

}
