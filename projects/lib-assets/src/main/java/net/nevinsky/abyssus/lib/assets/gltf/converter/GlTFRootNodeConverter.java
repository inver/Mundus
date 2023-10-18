package net.nevinsky.abyssus.lib.assets.gltf.converter;

import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.node.Node;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFNodeDto;
import net.nevinsky.abyssus.lib.assets.gltf.glb.GlTFBinary;
import org.apache.commons.collections4.CollectionUtils;

@RequiredArgsConstructor
public class GlTFRootNodeConverter {

    private final GlTFMeshConverter meshConverter;
    private final GlTFMaterialConverter materialConverter;
    private final MaterialHolder materialHolder;

    public Model convert(GlTFDto root, GlTFBinary binary, GlTFNodeDto nodeDto) {
        var res = new Model();

        var node = convertNode(root, binary, nodeDto);
        if (CollectionUtils.isNotEmpty(nodeDto.getChildren())) {
            nodeDto.getChildren().forEach(child -> {
                var childNode = root.getNodes().get(child);
                node.addChild(convertNode(root, binary, childNode));
            });
        }
        res.getNodes().add(node);

        return res;
    }

    public Node convertNode(GlTFDto root, GlTFBinary binary, GlTFNodeDto node) {
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
            meshConverter.load(root, binary, res, mesh, materialHolder.getDefaultMaterial(),
                    materialConverter.converts(root));
        }
        return res;
    }
}
