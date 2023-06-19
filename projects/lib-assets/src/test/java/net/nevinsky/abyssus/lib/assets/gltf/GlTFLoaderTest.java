package net.nevinsky.abyssus.lib.assets.gltf;

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
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

public class GlTFLoaderTest {
    private static final ObjectMapper mapper = JsonUtils.createMapper();
    private static GlTFLoader glTFLoader;
    private static GlTFMaterialConverter materialConverter;
    private static GlTFMeshConverter meshConverter;
    private static GlTFRootNodeConverter modelConverter;
    private static GlTFSceneConverter sceneConverter;
    private static GlTFTextureConverter textureConverter;
    private static TextureHolder textureHolder;
    private static final GlTFBinaryReader binaryReader = new GlTFBinaryReader();

    @BeforeAll
    public static void init() {
        var holder = new MaterialHolder();
        textureHolder = new TextureHolder();
        meshConverter = new GlTFMeshConverter();
        textureConverter = new GlTFTextureConverter(textureHolder);
        materialConverter = new GlTFMaterialConverter(textureConverter);
        modelConverter = new GlTFRootNodeConverter(meshConverter, materialConverter, holder);
        sceneConverter = new GlTFSceneConverter(modelConverter);

        glTFLoader = new GlTFLoader(
                mapper,
                sceneConverter,
                binaryReader
        );
    }

    @SneakyThrows
    @Test
    public void testLoadingAndSaveScene() {
        var scenes = new AbyssusScenes();

        var content = String.join("", IOUtils.readLines(
                getClass().getClassLoader().getResourceAsStream("gltf/aBeautifulGame/file.gltf"),
                Charset.defaultCharset()
        ));

        var dto = mapper.readValue(content, GlTFDto.class);
        var binary = new GlTFBinary(dto, getClass().getClassLoader().getResourceAsStream("gltf/aBeautifulGame/"
                + dto.getBuffers().get(0).getUri()));
        glTFLoader.load(scenes, dto, binary);

        Assertions.assertEquals(dto.getScenes().size(), scenes.getScenes().size());
        Assertions.assertEquals(dto.getScenes().get(0).getNodes().size(), scenes.getScenes().get(0).getModels().size());

        Assertions.assertNotNull(binary);

    }
}
