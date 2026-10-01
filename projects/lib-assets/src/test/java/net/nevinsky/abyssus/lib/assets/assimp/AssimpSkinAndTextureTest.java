package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import net.nevinsky.abyssus.core.model.ModelData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssimpSkinAndTextureTest {

    private static FileHandle resource(String path) {
        return new FileHandle(new File(Objects.requireNonNull(
                AssimpSkinAndTextureTest.class.getResource(path)).getFile()));
    }

    private static ModelNode find(ModelNode node, String id) {
        if (id.equals(node.id)) {
            return node;
        }
        if (node.children != null) {
            for (var c : node.children) {
                var f = find(c, id);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    @Test
    void skinnedMeshHasBoneWeightAttributesAndBoundBones() {
        ModelData data = new AssimpModelDataLoader().load("skinned", resource("/gltf/skinned.gltf"));

        var mesh = data.meshes.first();
        int boneAttrs = 0;
        for (var a : mesh.attributes) {
            if (a.usage == Usage.BoneWeight) {
                boneAttrs++;
            }
        }
        assertEquals(MeshProcessor.BONE_WEIGHTS, boneAttrs);

        var skinned = find(data.nodes.first(), "skinned");
        assertNotNull(skinned);
        var bones = skinned.parts[0].bones;
        assertNotNull(bones);
        assertEquals(2, bones.size);
        assertTrue(bones.containsKey("root_joint"));
        assertTrue(bones.containsKey("child_joint"));
        // inverse bind matrix of child joint translates by (0, -1, 0)
        assertEquals(-1f, bones.get("child_joint").val[13], 1e-5f);
        // every bone id resolves to a real node
        assertNotNull(find(data.nodes.first(), "child_joint"));
    }

    @Test
    void animationChannelsTargetNodeIds() {
        ModelData data = new AssimpModelDataLoader().load("skinned", resource("/gltf/skinned.gltf"));

        assertEquals(1, data.animations.size);
        var animation = data.animations.first();
        assertEquals("wave", animation.id);
        assertEquals(2, animation.nodeAnimations.size);

        var rotation = animation.nodeAnimations.select(n -> n.nodeId.equals("child_joint")).iterator().next();
        assertNotNull(rotation.rotation);
        assertEquals(2, rotation.rotation.size);
        assertEquals(0f, rotation.rotation.get(0).keytime, 1e-4f);
        assertEquals(1f, rotation.rotation.get(1).keytime, 1e-3f, "key times are in seconds");
        assertEquals(0.7071f, rotation.rotation.get(1).value.z, 1e-3f);

        var translation = animation.nodeAnimations.select(n -> n.nodeId.equals("root_joint")).iterator().next();
        assertNotNull(translation.translation);
        assertEquals(1f, translation.translation.get(1).value.x, 1e-5f);
    }

    @Test
    void embeddedTextureIsExtractedOnce(@TempDir Path tmp) {
        var out = new FileHandle(tmp.toFile());
        ModelData data = new AssimpModelDataLoader().load("tex", resource("/gltf/embedded_texture.gltf"),
                AssimpFlags.DEFAULT, out);

        var material = data.materials.first();
        assertEquals(1, material.textures.size);
        ModelTexture texture = material.textures.first();
        assertEquals(ModelTexture.USAGE_DIFFUSE, texture.usage);
        var file = new File(texture.fileName);
        assertTrue(file.isFile(), "embedded texture must be written to " + texture.fileName);
        assertTrue(file.length() > 0);
        assertEquals(tmp.toFile().getAbsoluteFile(), file.getParentFile().getAbsoluteFile());
    }

    @Test
    void embeddedTextureIsSkippedWithoutOutputDir() {
        ModelData data = new AssimpModelDataLoader().load("tex", resource("/gltf/embedded_texture.gltf"));
        assertEquals(0, data.materials.first().textures.size);
    }
}
