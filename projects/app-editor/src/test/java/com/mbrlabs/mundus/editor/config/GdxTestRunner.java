package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.kotcrab.vis.ui.VisUI;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class GdxTestRunner extends SpringExtension {

    public GdxTestRunner() {
        GdxNativesLoader.load();
        var gl20 = Mockito.mock(GL20.class);
        Mockito.when(gl20.glCreateShader(ArgumentMatchers.anyInt())).thenReturn(1);

        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
        Gdx.files = new HeadlessFiles();
        Gdx.app = Mockito.mock(Application.class);

        Mockito.when(Gdx.app.getPreferences(ArgumentMatchers.any())).thenReturn(Mockito.mock(Preferences.class));

        var graphics = Mockito.mock(Graphics.class);
        Gdx.graphics = graphics;

        Mockito.when(graphics.getWidth()).thenReturn(1024);
        Mockito.when(graphics.getHeight()).thenReturn(768);

        Gdx.input = Mockito.mock(Input.class);

        //for parallel running
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
    }

}