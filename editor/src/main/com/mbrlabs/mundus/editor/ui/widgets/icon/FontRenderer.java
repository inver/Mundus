package com.mbrlabs.mundus.editor.ui.widgets.icon;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FontRenderer {
    private final BitmapFont font;

    public FontRenderer(FileHandle fontFile) {
        var fontGenerator = new FreeTypeFontGenerator(fontFile);
        var generatorParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        generatorParameter.size = 24;
        generatorParameter.padTop = 2;
        generatorParameter.characters =
                Arrays.stream(SymbolIcon.values()).map(SymbolIcon::getSymbol).collect(Collectors.joining());
        font = fontGenerator.generateFont(generatorParameter);
        fontGenerator.dispose();
    }

    public BitmapFont getFont() {
        return font;
    }
}
