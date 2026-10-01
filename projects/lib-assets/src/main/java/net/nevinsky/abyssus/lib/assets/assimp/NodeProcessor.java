package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;
import net.nevinsky.abyssus.core.model.ModelMesh;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Walks the Assimp node tree, producing {@link ModelNode}s with unique ids and decomposed local transforms.
 */
final class NodeProcessor {

    private final Map<Integer, ModelMesh> meshesByIndex;
    private final Map<Integer, String> materialIdByMeshIndex;
    private final Map<Integer, MeshProcessor.Skin> skinsByMeshIndex;
    private final Set<String> usedIds = new HashSet<>();
    private final Map<String, String> idsByName = new HashMap<>();
    private final List<PendingSkin> pendingSkins = new ArrayList<>();

    private static final class PendingSkin {
        final ModelNodePart part;
        final MeshProcessor.Skin skin;

        PendingSkin(ModelNodePart part, MeshProcessor.Skin skin) {
            this.part = part;
            this.skin = skin;
        }
    }

    NodeProcessor(Map<Integer, ModelMesh> meshesByIndex, Map<Integer, String> materialIdByMeshIndex,
                  Map<Integer, MeshProcessor.Skin> skinsByMeshIndex) {
        this.meshesByIndex = meshesByIndex;
        this.materialIdByMeshIndex = materialIdByMeshIndex;
        this.skinsByMeshIndex = skinsByMeshIndex;
    }

    /** Converts the tree and binds skin bones to the (unique) node ids. */
    ModelNode process(AINode root) {
        var node = build(root);
        for (var pending : pendingSkins) {
            var bones = new ArrayMap<String, Matrix4>(pending.skin.names.size());
            for (int i = 0; i < pending.skin.names.size(); i++) {
                var name = pending.skin.names.get(i);
                bones.put(idsByName.getOrDefault(name, name), pending.skin.offsets.get(i));
            }
            pending.part.bones = bones;
        }
        return node;
    }

    /** @return the id of the first node with the given Assimp name, or the name itself if there is none */
    String idForName(String name) {
        return idsByName.getOrDefault(name, name);
    }

    private ModelNode build(AINode aiNode) {
        var node = new ModelNode();
        var name = aiNode.mName().dataString();
        node.id = uniqueId(name);
        idsByName.putIfAbsent(name, node.id);

        var matrix = toMatrix4(aiNode.mTransformation());
        var translation = matrix.getTranslation(new Vector3());
        var scale = matrix.getScale(new Vector3());
        var rotation = matrix.getRotation(new Quaternion(), true);
        node.translation = translation.isZero() ? null : translation;
        node.scale = scale.epsilonEquals(1f, 1f, 1f, 1e-6f) ? null : scale;
        node.rotation = rotation.isIdentity() ? null : rotation;

        var parts = new java.util.ArrayList<ModelNodePart>();
        var meshIndices = aiNode.mMeshes();
        for (int i = 0; i < aiNode.mNumMeshes(); i++) {
            int meshIndex = meshIndices.get(i);
            var mesh = meshesByIndex.get(meshIndex);
            if (mesh == null) {
                continue; // skipped (non-triangle) mesh
            }
            var nodePart = new ModelNodePart();
            nodePart.meshPartId = mesh.parts[0].id;
            nodePart.materialId = materialIdByMeshIndex.get(meshIndex);
            var skin = skinsByMeshIndex.get(meshIndex);
            if (skin != null) {
                pendingSkins.add(new PendingSkin(nodePart, skin));
            }
            parts.add(nodePart);
            if (node.meshId == null) {
                node.meshId = mesh.id;
            }
        }
        if (!parts.isEmpty()) {
            node.parts = parts.toArray(new ModelNodePart[0]);
        }

        int childCount = aiNode.mNumChildren();
        if (childCount > 0) {
            node.children = new ModelNode[childCount];
            var children = aiNode.mChildren();
            for (int i = 0; i < childCount; i++) {
                node.children[i] = build(AINode.create(children.get(i)));
            }
        }
        return node;
    }

    private String uniqueId(String name) {
        var base = name == null || name.isEmpty() ? "node" : name;
        var id = base;
        for (int n = 1; !usedIds.add(id); n++) {
            id = base + "_" + n;
        }
        return id;
    }

    /** Assimp matrices are row-major, LibGDX {@link Matrix4#set(float[])} is column-major. */
    static Matrix4 toMatrix4(AIMatrix4x4 m) {
        return new Matrix4(new float[]{
                m.a1(), m.b1(), m.c1(), m.d1(),
                m.a2(), m.b2(), m.c2(), m.d2(),
                m.a3(), m.b3(), m.c3(), m.d3(),
                m.a4(), m.b4(), m.c4(), m.d4()});
    }
}
