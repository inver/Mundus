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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
public class ImageChooserField extends VisTable {

    private static final Drawable PLACEHOLDER_IMG = new TextureRegionDrawable(
            new TextureRegion(new Texture(Gdx.files.internal("ui/img_placeholder.png"))));

    private final int width;

    private final VisTextButton selectButton = new VisTextButton("Select");

    private final Image img = new Image(PLACEHOLDER_IMG);
    private Texture texture;
    private FileHandle fileHandle;

    public ImageChooserField(int width) {
        this(width, null);
    }

    public ImageChooserField(int width, String buttonText) {
        super();
        this.width = width;

        setupUI();
        if (buttonText != null) {
            setButtonText(buttonText);
        }
    }

    public FileHandle getFile() {
        return fileHandle;
    }

    public void removeImage() {
        img.setDrawable(PLACEHOLDER_IMG);
    }

    public void setButtonText(String text) {
        selectButton.setText(text);
    }

    public void setImage(FileHandle fileHandle) {
        if (texture != null) {
            texture.dispose();
        }

        this.fileHandle = fileHandle;

        if (fileHandle != null) {
            texture = new Texture(fileHandle);
            img.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        } else {
            img.setDrawable(PLACEHOLDER_IMG);
        }
    }

    private void setupUI() {
        pad(4);
        add(img).width(width).height(width).expandX().fillX().row();
        add(selectButton).width(width).padTop(4).expandX();
    }

    public VisTextButton getSelectButton() {
        return selectButton;
    }
}
