package com.mbrlabs.mundus.editor.ui.widgets.icon;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public class FontRenderer {
    private final BitmapFont regular;
    private final BitmapFont small;
    private final BitmapFont tiny;

    public FontRenderer(FileHandle fontFile) {
        var fontGenerator = new FreeTypeFontGenerator(fontFile);
        var generatorParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        generatorParameter.size = 24;
        generatorParameter.padTop = 2;
        generatorParameter.characters =
                Arrays.stream(SymbolIcon.values()).map(SymbolIcon::getSymbol).collect(Collectors.joining());
        regular = fontGenerator.generateFont(generatorParameter);

        generatorParameter.size = 20;
        small = fontGenerator.generateFont(generatorParameter);

        generatorParameter.size = 16;
        tiny = fontGenerator.generateFont(generatorParameter);

        fontGenerator.dispose();
    }
}
