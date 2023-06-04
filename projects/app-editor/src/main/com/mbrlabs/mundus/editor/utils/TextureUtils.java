package com.mbrlabs.mundus.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class TextureUtils {

    //todo cache this
    public static TextureRegionDrawable load(String imagePath, int width, int height) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(imagePath)), width, height));
    }
}
