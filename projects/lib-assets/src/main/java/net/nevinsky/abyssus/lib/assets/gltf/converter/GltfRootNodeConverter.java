package net.nevinsky.abyssus.lib.assets.gltf.converter;

import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.node.Node;
import net.nevinsky.abyssus.core.node.NodePart;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFNodeDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.binary.GlTFBinary;
import org.apache.commons.collections4.CollectionUtils;

@RequiredArgsConstructor
public class GltfRootNodeConverter {

    private final GltfMeshConverter meshConverter;
    private final GltfNodeConverter nodeConverter;
    private final GltfMaterialConverter materialConverter;

    public Model convert(GlTFDto root, GlTFBinary binary, GlTFNodeDto nodeDto) {
        var res = new Model();

        var node = convert(root, nodeDto);
        if (CollectionUtils.isNotEmpty(nodeDto.getChildren())) {
            nodeDto.getChildren().forEach(child -> {
                var childNode = root.getNodes().get(child);
                node.addChild(convert(root, childNode));
            });
        }
        res.getNodes().add(node);

        return res;
    }

    public Node convert(GlTFDto root, GlTFNodeDto node) {
        var res = new Node();
        res.id = node.getName();
        if (node.getTranslation() != null) {
            res.translation.set(node.getTranslation());
        }
        if (node.getRotation() != null) {
            res.rotation.set(node.getRotation()[0], node.getRotation()[1],
                    node.getRotation()[2], node.getRotation()[3]);
        }
        if (node.getScale() != null) {
            res.scale.set(node.getScale());
        }
        if (node.getMatrix() != null) {
            throw new UnsupportedOperationException("Converter unsupports matrix of transformation");
        }
        if (node.getMesh() != null) {
            var mesh = root.getMeshes().get(node.getMesh());
            var part = new NodePart();


        }


        return res;
    }
}
