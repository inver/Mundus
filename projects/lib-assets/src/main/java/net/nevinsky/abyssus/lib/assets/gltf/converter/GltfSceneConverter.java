package net.nevinsky.abyssus.lib.assets.gltf.converter;

import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFSceneDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.binary.GlTFBinary;
import net.nevinsky.abyssus.lib.assets.gltf.scene.Scene;

@RequiredArgsConstructor
public class GltfSceneConverter {

    private final GltfRootNodeConverter modelConverter;

    public void fillScene(GlTFDto root, GlTFBinary binary, GlTFSceneDto dto, Scene scene) {
        scene.setName(dto.getName());
        dto.getNodes().forEach(nodeIndex -> {
            var node = root.getNodes().get(nodeIndex);
            scene.addModel(modelConverter.convert(root, binary, node));
        });
    }

    public GlTFSceneDto convert(Scene scene) {
        return null;
    }
}
