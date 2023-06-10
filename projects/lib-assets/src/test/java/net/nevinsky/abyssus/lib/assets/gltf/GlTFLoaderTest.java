package net.nevinsky.abyssus.lib.assets.gltf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GltfMaterialConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GltfMeshConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GltfNodeConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GltfRootNodeConverter;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GltfSceneConverter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.binary.GlTFBinary;
import net.nevinsky.abyssus.lib.assets.gltf.scene.AbyssusScenes;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;

public class GlTFLoaderTest {
    private static final ObjectMapper mapper = JsonUtils.createMapper();
    private static GlTFLoader glTFLoader;
    private static GltfMaterialConverter materialConverter;
    private static GltfNodeConverter nodeConverter;
    private static GltfMeshConverter meshConverter;
    private static GltfRootNodeConverter modelConverter;
    private static GltfSceneConverter sceneConverter;
    private static final GlTFBinaryReader binaryReader = new GlTFBinaryReader();

    @BeforeAll
    public static void init() {
        meshConverter = new GltfMeshConverter();
        nodeConverter = new GltfNodeConverter();
        materialConverter = new GltfMaterialConverter();
        modelConverter = new GltfRootNodeConverter(meshConverter, nodeConverter, materialConverter);
        sceneConverter = new GltfSceneConverter(modelConverter);

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
        GlTFBinary binary = null;
        glTFLoader.load(scenes, dto, binary);

        Assertions.assertEquals(dto.getScenes().size(), scenes.getScenes().size());
        Assertions.assertEquals(dto.getScenes().get(0).getNodes().size(), scenes.getScenes().get(0).getModels().size());

        Assertions.assertNotNull(binary);

    }
}
