package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssimpModelExporterTest {

    private final AssimpModelExporter exporter = new AssimpModelExporter();
    private final AssimpModelDataLoader loader = new AssimpModelDataLoader();

    private static FileHandle resource(String path) {
        return new FileHandle(new File(Objects.requireNonNull(
                AssimpModelExporterTest.class.getResource(path)).getFile()));
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
    void exportCopiesTexturesKeepingSubfolders(@TempDir Path tmp) {
        var out = new FileHandle(tmp.toFile());
        exporter.exportGltf(resource("/obj/tri.obj"), out.child("model.gltf"));

        assertTrue(out.child("model.gltf").exists());
        assertTrue(out.child("model.bin").exists());
        assertTrue(out.child("textures/diffuse.png").exists());
        assertTrue(out.child("textures/bump.png").exists());
    }

    @Test
    void exportedObjCanBeReloadedWithTextures(@TempDir Path tmp) {
        var out = new FileHandle(tmp.toFile());
        exporter.exportGltf(resource("/obj/tri.obj"), out.child("model.gltf"));

        var data = loader.load("tri", out.child("model.gltf"));
        assertEquals(1, data.meshes.size);
        assertTrue(data.materials.first().textures.size >= 1);
        for (var texture : data.materials.first().textures) {
            assertTrue(new File(texture.fileName).isFile(), "texture must exist: " + texture.fileName);
        }
    }

    @Test
    void skinAndAnimationSurviveExportRoundTrip(@TempDir Path tmp) {
        var out = new FileHandle(tmp.toFile());
        exporter.exportGltf(resource("/gltf/skinned.gltf"), out.child("model.gltf"));

        var data = loader.load("skinned", out.child("model.gltf"));
        var skinned = find(data.nodes.first(), "skinned");
        assertNotNull(skinned, "mesh node must survive");
        assertNotNull(skinned.parts[0].bones, "skin must survive");
        assertEquals(2, skinned.parts[0].bones.size);
        assertNotNull(find(data.nodes.first(), "child_joint"));
        assertEquals(1, data.animations.size);
        assertEquals(2, data.animations.first().nodeAnimations.size);
    }

    @Test
    void embeddedTextureSurvivesExportRoundTrip(@TempDir Path tmp) {
        var out = new FileHandle(tmp.toFile());
        exporter.exportGltf(resource("/gltf/embedded_texture.gltf"), out.child("model.gltf"));

        var embeddedDir = out.child("embedded");
        var data = loader.load("tex", out.child("model.gltf"), AssimpFlags.DEFAULT, embeddedDir);
        assertEquals(1, data.materials.first().textures.size);
        assertTrue(new File(data.materials.first().textures.first().fileName).isFile());
    }

    @Test
    void exportOfMissingFileFails(@TempDir Path tmp) {
        var out = new FileHandle(tmp.toFile());
        assertThrows(AssimpImportException.class,
                () -> exporter.exportGltf(new FileHandle("/does/not/exist.obj"), out.child("model.gltf")));
    }
}
