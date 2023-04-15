package com.mbrlabs.mundus.commons.importExport;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.BaseTest;
import com.mbrlabs.mundus.commons.importExport.gltf.model.Gltf;
import com.mbrlabs.mundus.commons.loader.AssimpModelLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.model.Model;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class GltfExportTest extends BaseTest {

    private final AssimpModelLoader loader = new AssimpModelLoader();
    private AssimpExporter exporter;

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

        exporter = new AssimpExporter();
//        exporter = new GltfExporter(new ObjectMapper(), new GltfMeshExporter(), new GltfMaterialExporter(),
//                new GltfSkinExporter(), new GltfAnimationExporter());
    }

    @SneakyThrows
    @Test
    public void testExportLargeModel() {
        var modelData = loader.loadModelData("ololo", getHandle("/obj/sr22/sr22.obj"));
        var model = new Model(modelData);

        String fileName = "/tmp/" + UUID.randomUUID() + ".gltf";
        exporter.export(model, "gltf", fileName);
        String file = String.join("", IOUtils.readLines(new FileReader(fileName)));
        log.info(file);
        var obj = new ObjectMapper().readValue(file, Gltf.class);
        Assert.assertNotNull(obj);
    }
}
