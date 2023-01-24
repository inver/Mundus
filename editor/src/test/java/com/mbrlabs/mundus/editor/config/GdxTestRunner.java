package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.kotcrab.vis.ui.VisUI;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GdxTestRunner extends SpringJUnit4ClassRunner {

    public GdxTestRunner(Class<?> klass) throws InitializationError {
        super(klass);

        Lwjgl3NativesLoader.load();
        var gl20 = mock(GL20.class);
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
            VisUI.load();
        }
    }

}