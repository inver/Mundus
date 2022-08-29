package com.mbrlabs.mundus.commons.ac3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.ac3d.core.ColorDto;
import com.mbrlabs.mundus.commons.ac3d.dto.Ac3dMaterial;
import com.mbrlabs.mundus.commons.ac3d.dto.Ac3dModel;
import com.mbrlabs.mundus.commons.ac3d.dto.Ac3dObject;
import org.apache.commons.collections4.CollectionUtils;

public class Ac3dToLibGdxConverter {

    public ModelData convert(Ac3dModel model) {
        var res = new ModelData();
        for (int i = 0; i < model.getMaterials().size(); i++) {
            res.materials.add(convert(model.getMaterials().get(i), i));
        }
        for (var obj : model.getObjects()) {
            convert(obj, res, null, 0);
        }
        return res;
    }

    public ModelMaterial convert(Ac3dMaterial input, int index) {
        var res = new ModelMaterial();
        res.id = index + "";
        res.diffuse = fromDto(input.getDiffuse());
        res.ambient = fromDto(input.getAmbient());
        res.emissive = fromDto(input.getEmissive());
        res.specular = fromDto(input.getSpecular());
        res.shininess = input.getShininess();
        res.opacity = input.getTransparency();
        return res;
    }

    public void convert(Ac3dObject obj, ModelData res, ModelNode parent, int index) {
        var node = new ModelNode();
        node.id = "node_" + obj.getName();
        if (obj.getTranslation() != null) {
            node.translation = new Vector3(obj.getTranslation().getX(), obj.getTranslation().getY(), obj.getTranslation().getZ());
        }
        //todo
//        node.rotation = new Quaternion(obj.getRotation())
//        node.scale
        if (parent == null) {
            res.nodes.add(node);
        } else {
            parent.children[index] = node;
        }

        if (CollectionUtils.isNotEmpty(obj.getVertices()) || CollectionUtils.isNotEmpty(obj.getSurfaces())) {
            var mesh = new ModelMesh();
            mesh.id = "mesh_" + obj.getName();
            if (CollectionUtils.isNotEmpty(obj.getVertices())) {
                mesh.attributes = new VertexAttribute[]{VertexAttribute.Position()};
                mesh.vertices = new float[obj.getVertices().size() * 3];
                for (int i = 0; i < obj.getVertices().size(); i++) {
                    var vertex = obj.getVertices().get(i);
                    mesh.vertices[i * 3] = vertex.getX();
                    mesh.vertices[i * 3 + 1] = vertex.getY();
                    mesh.vertices[i * 3 + 2] = vertex.getZ();
                }
            }

            if (CollectionUtils.isNotEmpty(obj.getSurfaces())) {
                mesh.parts = new ModelMeshPart[obj.getSurfaces().size()];
                node.parts = new ModelNodePart[obj.getSurfaces().size()];
                for (int i = 0; i < obj.getSurfaces().size(); i++) {
                    var surface = obj.getSurfaces().get(i);

                    var part = new ModelMeshPart();
                    part.id = mesh.id + "_part_" + i;
                    part.indices = new short[surface.getVerticesRefs().size()];
                    for (int j = 0; j < surface.getVerticesRefs().size(); j++) {
                        part.indices[j] = surface.getVerticesRefs().get(j).getKey().shortValue();
                    }
                    if (surface.getVerticesRefs().size() == 4) {
                        part.primitiveType = GL20.GL_TRIANGLE_STRIP;
                    } else {
                        part.primitiveType = GL20.GL_TRIANGLES;
                    }
                    mesh.parts[i] = part;

                    var nodePart = new ModelNodePart();
                    nodePart.meshPartId = part.id;
                    nodePart.materialId = surface.getMaterialIndex() + "";
                    node.parts[i] = nodePart;
                }
            }
            res.meshes.add(mesh);
        }

        if (CollectionUtils.isNotEmpty(obj.getChildren())) {
            for (int i = 0; i < obj.getChildren().size(); i++) {
                node.children = new ModelNode[obj.getChildren().size()];
                convert(obj.getChildren().get(i), res, node, i);
            }
        }
    }

    public ModelNode convert2Node(Ac3dObject obj, int i) {
        var res = new ModelNode();
        res.id = "node_" + i;
        return res;
    }

    private Color fromDto(ColorDto input) {
        return new Color(input.getR(), input.getG(), input.getB(), input.getA());
    }
}
