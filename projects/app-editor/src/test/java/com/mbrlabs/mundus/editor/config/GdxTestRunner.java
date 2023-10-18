package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.mbrlabs.mundus.editor.ui.widgets.icon.FontRenderer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.IntBuffer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GdxTestRunner extends SpringExtension {

    public GdxTestRunner() {
        Lwjgl3NativesLoader.load();
        var gl20 = mock(GL20.class);
        when(gl20.glCreateShader(anyInt())).thenReturn(1);
        doAnswer(invocation -> {
            Integer arg0 = invocation.getArgument(0);
            IntBuffer arg1 = invocation.getArgument(1);

            arg1.put(32);
            return null;
        }).when(gl20).glGetIntegerv(any(Integer.class), any(IntBuffer.class));

        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
        Gdx.files = new Lwjgl3Files();
        Gdx.app = mock(Application.class);

        when(Gdx.app.getPreferences(any())).thenReturn(mock(Preferences.class));

        var graphics = mock(Graphics.class);
        Gdx.graphics = graphics;

        when(graphics.getWidth()).thenReturn(1024);
        when(graphics.getHeight()).thenReturn(768);

        Gdx.input = mock(Input.class);

        //for parallel running
        if (!VisUI.isLoaded()) {
            initVisUI();
        }
    }

    private void initVisUI() {
        var generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/robotoRegular.ttf"));
        var params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.kerning = true;
        params.borderStraight = false;
        params.genMipMaps = true;
        params.hinting = FreeTypeFontGenerator.Hinting.Full;

        // font norm
        params.size = 12;
        var fontNorm = generator.generateFont(params);

        // font small
        params.size = 11;
        var fontSmall = generator.generateFont(params);

        // font tiny
        params.size = 10;
        var fontTiny = generator.generateFont(params);
        generator.dispose();

        //todo add several sizes of icons
        var symbolFontRenderer = new FontRenderer(Gdx.files.internal("fonts/materialSymbolsRounded.ttf"));

        // skin
        var skin = new Skin();
        skin.add("font-norm", fontNorm, BitmapFont.class);
        skin.add("font-small", fontSmall, BitmapFont.class);
        skin.add("font-tiny", fontTiny, BitmapFont.class);
        skin.add("font-symbol", symbolFontRenderer.getFont(), BitmapFont.class);

        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/skin/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/skin/uiskin.json"));
        VisUI.load(skin);
    }

}