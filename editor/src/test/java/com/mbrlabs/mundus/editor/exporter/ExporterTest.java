package com.mbrlabs.mundus.editor.exporter;

import com.badlogic.gdx.utils.JsonWriter;
import com.mbrlabs.mundus.commons.dto.GameObjectDto;
import com.mbrlabs.mundus.commons.dto.SceneDto;
import com.mbrlabs.mundus.commons.dto.TerrainComponentDto;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import com.mbrlabs.mundus.editor.core.project.ProjectStorage;
import com.mbrlabs.mundus.editor.core.scene.SceneStorage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Ignore
public class ExporterTest {

    private Exporter exporter;

    @Before
    public void setUp() {
        ProjectStorage manager = mock(ProjectStorage.class);
        ProjectContext context = mock(ProjectContext.class);
        SceneStorage sceneStorage = mock(SceneStorage.class);
        EditorAssetManager assetManager = mock(EditorAssetManager.class);

        exporter = new Exporter(manager, context, sceneStorage, assetManager);
    }

    @Test
    public void testExportEmptyScene() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);

        SceneDto scene = new SceneDto();
        scene.setName("Scene 1");
        exporter.exportScene(scene, writer, JsonWriter.OutputType.json);

        String result = baos.toString();
        assertEquals("{\"id\":0,\"name\":\"Scene 1\",\"gos\":[]}", result);
    }

    @Test
    public void testExportSimpleScene() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);

        GameObjectDto terrain = buildTerrain("Terrain 1");
        SceneDto scene = new SceneDto();
        scene.setName("Scene 1");
        scene.getGameObjects().add(terrain);

        exporter.exportScene(scene, writer, JsonWriter.OutputType.json);

        String result = baos.toString();
        assertEquals("{\"id\":0,\"name\":\"Scene 1\",\"gos\":[{\"i\":0,\"n\":\"Terrain 1\",\"a\":false,\"t\":[0,0,0,0,0,0,0,0,0,0],\"g\":[\"grass\"],\"ct\":{\"i\":null}}]}", result);
    }

    private GameObjectDto buildTerrain(String name) {
        GameObjectDto terrain = new GameObjectDto();
        terrain.setName(name);

        TerrainComponentDto terrainComponent = new TerrainComponentDto();
        terrain.setTerrainComponent(terrainComponent);

        List<String> tags = new ArrayList<>();
        tags.add("grass");
        terrain.setTags(tags);

        return terrain;
    }

}
