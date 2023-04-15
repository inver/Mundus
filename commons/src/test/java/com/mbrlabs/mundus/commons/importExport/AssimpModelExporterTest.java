package com.mbrlabs.mundus.commons.importExport;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.mbrlabs.mundus.commons.BaseTest;
import com.mbrlabs.mundus.commons.loader.AssimpModelLoader;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.model.Model;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class AssimpModelExporterTest extends BaseTest {

    private final AssimpModelLoader loader = new AssimpModelLoader();


    @Before
    public void init() {
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

    }

    @Test
    public void testSimpleExport() {
        var modelData = loader.loadModelData("ololo", getHandle("/obj/sr22/sr22.obj"));
        var model = new Model(modelData);

        var guid = UUID.randomUUID().toString();
//        exporter.export(model, new FileHandle("/tmp/assimp_test/" + guid));

    }
}
