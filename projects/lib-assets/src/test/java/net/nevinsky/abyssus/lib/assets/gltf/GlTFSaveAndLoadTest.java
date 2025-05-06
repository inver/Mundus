package net.nevinsky.abyssus.lib.assets.gltf;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GlTFMaterialConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GlTFMeshConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GlTFRootNodeConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GlTFSceneConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GlTFTextureConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.MaterialHolder;
import net.nevinsky.abyssus.lib.assets.gltf.converter.TextureHolder;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.glb.GlTFBinary;
import net.nevinsky.abyssus.lib.assets.gltf.scene.AbyssusScenes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlTFSaveAndLoadTest {
    private static final ObjectMapper mapper = JsonUtils.createMapper();
    private static GlTFLoader glTFLoader;
    private static GlTFSaver glTFSaver;
    private static GlTFMaterialConverter materialConverter;
    private static GlTFMeshConverter meshConverter;
    private static GlTFRootNodeConverter modelConverter;
    private static GlTFSceneConverter sceneConverter;
    private static GlTFTextureConverter textureConverter;
    private static TextureHolder textureHolder;
    private static final GlTFBinaryReader binaryReader = new GlTFBinaryReader();

    private static final GlTFBinaryWriter binaryWriter = new GlTFBinaryWriter();

    @BeforeAll
    public static void init() {
        Lwjgl3NativesLoader.load();
        var gl20 = mock(GL20.class);
        when(gl20.glCreateShader(anyInt())).thenReturn(1);

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

        var holder = new MaterialHolder();
        textureHolder = new TextureHolder();
        meshConverter = new GlTFMeshConverter();
        textureConverter = new GlTFTextureConverter(textureHolder);
        materialConverter = new GlTFMaterialConverter(textureConverter);
        modelConverter = new GlTFRootNodeConverter(meshConverter, materialConverter, holder);
        sceneConverter = new GlTFSceneConverter(modelConverter);

        glTFLoader = new GlTFLoader(sceneConverter);
        glTFSaver = new GlTFSaver(binaryWriter);
    }

    @SneakyThrows
    @Test
    public void testLoadingScene() {

        var dto = readContent("boxAnimated");
        var binary = readBinary(dto, "boxAnimated");

        var scenes = new AbyssusScenes();
        glTFLoader.load(scenes, dto, binary);

        Assertions.assertEquals(dto.getScenes().size(), scenes.getScenes().size());
        Assertions.assertEquals(dto.getScenes().get(0).getNodes().size(), scenes.getScenes().get(0).getModels().size());
        Assertions.assertNotNull(binary);
    }

    @SneakyThrows
    @Test
    public void testSavingScene() {
        var dto = readContent("boxAnimated");
        var binary = readBinary(dto, "boxAnimated");

        var scenes = new AbyssusScenes();
        glTFLoader.load(scenes, dto, binary);

        var resultFile = new File("/tmp/abyssusTests/" + UUID.randomUUID() + ".gltf");
        FileUtils.forceMkdir(resultFile);
        glTFSaver.save(scenes, resultFile);


    }

    private GlTFBinary readBinary(GlTFDto dto, String assetName) {
        return binaryReader.readFile(dto, getClass().getClassLoader().getResourceAsStream("gltf/" + assetName + "/"
                + dto.getBuffers().get(0).getUri()));
    }


    private GlTFDto readContent(String assetName) throws IOException {
        var content = String.join("", IOUtils.readLines(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("gltf/" + assetName + "/file.gltf")
                ),
                Charset.defaultCharset()
        ));

        return mapper.readValue(content, GlTFDto.class);
    }


}
