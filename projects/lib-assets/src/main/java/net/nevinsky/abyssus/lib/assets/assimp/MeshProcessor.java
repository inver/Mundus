package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.nevinsky.abyssus.core.model.ModelMesh;
import net.nevinsky.abyssus.core.model.ModelMeshPart;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.aiPrimitiveType_TRIANGLE;

/**
 * Converts an {@link AIMesh} into an interleaved {@link ModelMesh} with a single triangle part.
 * Attribute order: position, normal, color, texcoord0, texcoord1, tangent, binormal (each only if present), then
 * {@link #BONE_WEIGHTS} bone weight attributes (bone index, weight) for skinned meshes.
 */
final class MeshProcessor {

    static final int BONE_WEIGHTS = 4;

    /** Bones of a skinned mesh. The order of {@link #names} is the bone index used by the vertex attributes. */
    static final class Skin {
        final List<String> names = new ArrayList<>();
        final List<Matrix4> offsets = new ArrayList<>();
    }

    private final Map<Integer, Skin> skins = new HashMap<>();

    /** @return the skin of the processed mesh with the given index, or {@code null} if it is not skinned */
    Skin skinOf(int meshIndex) {
        return skins.get(meshIndex);
    }

    /**
     * @return the mesh, or {@code null} for non-triangle meshes (points/lines)
     */
    ModelMesh process(AIMesh aiMesh, int index) {
        if ((aiMesh.mPrimitiveTypes() & aiPrimitiveType_TRIANGLE) == 0) {
            return null;
        }
        int count = aiMesh.mNumVertices();
        var positions = aiMesh.mVertices();
        var normals = aiMesh.mNormals();
        var colors = aiMesh.mColors(0);
        var uv0 = aiMesh.mTextureCoords(0);
        var uv1 = aiMesh.mTextureCoords(1);
        var tangents = aiMesh.mTangents();
        var binormals = aiMesh.mBitangents();

        var skin = readSkin(aiMesh);
        var boneIds = new int[skin == null ? 0 : count * BONE_WEIGHTS];
        var boneWeights = new float[boneIds.length];
        if (skin != null) {
            readBoneWeights(aiMesh, boneIds, boneWeights);
            skins.put(index, skin);
        }

        List<VertexAttribute> attributes = new ArrayList<>();
        attributes.add(VertexAttribute.Position());
        if (normals != null) {
            attributes.add(VertexAttribute.Normal());
        }
        if (colors != null) {
            attributes.add(VertexAttribute.ColorUnpacked());
        }
        if (uv0 != null) {
            attributes.add(VertexAttribute.TexCoords(0));
        }
        if (uv1 != null) {
            attributes.add(VertexAttribute.TexCoords(1));
        }
        if (tangents != null) {
            attributes.add(VertexAttribute.Tangent());
        }
        if (binormals != null) {
            attributes.add(VertexAttribute.Binormal());
        }

        if (skin != null) {
            for (int k = 0; k < BONE_WEIGHTS; k++) {
                attributes.add(VertexAttribute.BoneWeight(k));
            }
        }

        int stride = 0;
        for (var a : attributes) {
            stride += a.numComponents;
        }

        var vertices = new float[count * stride];
        var min = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        var max = new Vector3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
        int pos = 0;
        for (int i = 0; i < count; i++) {
            var p = positions.get(i);
            pos = put(vertices, pos, p);
            min.set(Math.min(min.x, p.x()), Math.min(min.y, p.y()), Math.min(min.z, p.z()));
            max.set(Math.max(max.x, p.x()), Math.max(max.y, p.y()), Math.max(max.z, p.z()));
            if (normals != null) {
                pos = put(vertices, pos, normals.get(i));
            }
            if (colors != null) {
                var c = colors.get(i);
                vertices[pos++] = c.r();
                vertices[pos++] = c.g();
                vertices[pos++] = c.b();
                vertices[pos++] = c.a();
            }
            if (uv0 != null) {
                pos = putUv(vertices, pos, uv0.get(i));
            }
            if (uv1 != null) {
                pos = putUv(vertices, pos, uv1.get(i));
            }
            if (tangents != null) {
                pos = put(vertices, pos, tangents.get(i));
            }
            if (binormals != null) {
                pos = put(vertices, pos, binormals.get(i));
            }
            if (skin != null) {
                for (int k = 0; k < BONE_WEIGHTS; k++) {
                    vertices[pos++] = boneIds[i * BONE_WEIGHTS + k];
                    vertices[pos++] = boneWeights[i * BONE_WEIGHTS + k];
                }
            }
        }

        var mesh = new ModelMesh();
        mesh.id = "mesh_" + index;
        mesh.attributes = attributes.toArray(new VertexAttribute[0]);
        mesh.vertices = vertices;
        mesh.parts = new ModelMeshPart[]{processPart(aiMesh, index, count == 0 ? new BoundingBox() :
                new BoundingBox(min, max))};
        return mesh;
    }

    private static Skin readSkin(AIMesh aiMesh) {
        if (aiMesh.mNumBones() == 0) {
            return null;
        }
        var skin = new Skin();
        var bones = aiMesh.mBones();
        for (int b = 0; b < aiMesh.mNumBones(); b++) {
            var bone = AIBone.create(bones.get(b));
            skin.names.add(bone.mName().dataString());
            skin.offsets.add(NodeProcessor.toMatrix4(bone.mOffsetMatrix()));
        }
        return skin;
    }

    /** Keeps the {@link #BONE_WEIGHTS} strongest influences per vertex; unused slots stay (0, 0). */
    private static void readBoneWeights(AIMesh aiMesh, int[] ids, float[] weights) {
        var bones = aiMesh.mBones();
        for (int b = 0; b < aiMesh.mNumBones(); b++) {
            var bone = AIBone.create(bones.get(b));
            var vertexWeights = bone.mWeights();
            for (int w = 0; w < bone.mNumWeights(); w++) {
                var vw = vertexWeights.get(w);
                int base = vw.mVertexId() * BONE_WEIGHTS;
                int slot = -1;
                for (int k = 0; k < BONE_WEIGHTS; k++) {
                    if (weights[base + k] == 0f) {
                        slot = k;
                        break;
                    }
                    if (slot < 0 || weights[base + k] < weights[base + slot]) {
                        slot = k;
                    }
                }
                if (weights[base + slot] == 0f || vw.mWeight() > weights[base + slot]) {
                    ids[base + slot] = b;
                    weights[base + slot] = vw.mWeight();
                }
            }
        }
    }

    private static ModelMeshPart processPart(AIMesh aiMesh, int index, BoundingBox boundingBox) {
        var faces = aiMesh.mFaces();
        var indices = new int[aiMesh.mNumFaces() * 3];
        int n = 0;
        for (int i = 0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = faces.get(i);
            if (face.mNumIndices() != 3) {
                continue;
            }
            var buffer = face.mIndices();
            indices[n++] = buffer.get(0);
            indices[n++] = buffer.get(1);
            indices[n++] = buffer.get(2);
        }
        var part = new ModelMeshPart();
        part.id = "part_" + index;
        part.indices = n == indices.length ? indices : java.util.Arrays.copyOf(indices, n);
        part.primitiveType = GL20.GL_TRIANGLES;
        part.setBoundingBox(boundingBox);
        return part;
    }

    private static int put(float[] dst, int pos, AIVector3D v) {
        dst[pos++] = v.x();
        dst[pos++] = v.y();
        dst[pos++] = v.z();
        return pos;
    }

    /** The single place where the V coordinate is flipped (Assimp: origin bottom-left, LibGDX: top-left). */
    private static int putUv(float[] dst, int pos, AIVector3D v) {
        dst[pos++] = v.x();
        dst[pos++] = 1 - v.y();
        return pos;
    }
}
