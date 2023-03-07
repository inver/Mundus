package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.HashMap;
import java.util.Map;

public class AppModel extends Model {

    private Map<String, MeshPart> meshMap = new HashMap<>();

    public AppModel() {
    }

    public AppModel(ModelData modelData) {
        super(modelData);
    }

    public AppModel(ModelData modelData, TextureProvider textureProvider) {
        super(modelData, textureProvider);
    }

    @Override
    protected void loadMeshes(Iterable<ModelMesh> meshes) {
        super.loadMeshes(meshes);

        meshMap = new HashMap<>();
        for (int i = 0; i < meshParts.size; i++) {
            meshMap.put(meshParts.get(i).id, meshParts.get(i));
        }
    }

    @Override
    protected Node loadNode(ModelNode modelNode) {
        Node node = new Node();
        node.id = modelNode.id;

        if (modelNode.translation != null) node.translation.set(modelNode.translation);
        if (modelNode.rotation != null) node.rotation.set(modelNode.rotation);
        if (modelNode.scale != null) node.scale.set(modelNode.scale);

        if (modelNode.parts != null) {
            for (ModelNodePart modelNodePart : modelNode.parts) {
                MeshPart meshPart = null;
                Material meshMaterial = null;

                if (modelNodePart.meshPartId != null) {
                    meshPart = meshMap.get(modelNodePart.meshPartId);
                }

                if (modelNodePart.materialId != null) {
                    for (Material material : materials) {
                        if (modelNodePart.materialId.equals(material.id)) {
                            meshMaterial = material;
                            break;
                        }
                    }
                }

                if (meshPart == null || meshMaterial == null) throw new GdxRuntimeException("Invalid node: " + node.id);

                NodePart nodePart = new NodePart();
                nodePart.meshPart = meshPart;
                nodePart.material = meshMaterial;
                node.parts.add(nodePart);
                //todo
//                if (modelNodePart.bones != null) {
//                    nodePartBones.put(nodePart, modelNodePart.bones);
//                }
            }
        }

        if (modelNode.children != null) {
            for (ModelNode child : modelNode.children) {
                node.addChild(loadNode(child));
            }
        }

        return node;
    }
}
