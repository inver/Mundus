package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;


public class FontAwesomeIcon extends BaseDrawable {

    private final BitmapFont font;
    private final String iconSymbol;
    private Color color = Color.WHITE;

    public FontAwesomeIcon(BitmapFont font, String iconSymbol) {
        this.font = font;
        this.iconSymbol = iconSymbol;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        font.setColor(color);
        font.draw(batch, iconSymbol, x+3, y + 15);
    }

    @Override
    public float getMinHeight() {
        return 15f;
    }

    @Override
    public float getMinWidth() {
        return 19f;
    }
}
