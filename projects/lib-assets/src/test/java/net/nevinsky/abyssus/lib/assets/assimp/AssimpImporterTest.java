package net.nevinsky.abyssus.lib.assets.assimp;

import org.junit.jupiter.api.Test;
import org.lwjgl.assimp.AINode;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssimpImporterTest {

    private static String resource(String path) {
        return new File(Objects.requireNonNull(AssimpImporterTest.class.getResource(path)).getFile()).getPath();
    }

    @Test
    void importKeepsNodeHierarchy() {
        try (var imported = AssimpImporter.importScene(resource("/gltf/hierarchy.gltf"), AssimpFlags.DEFAULT)) {
            var root = imported.scene().mRootNode();
            assertNotNull(root);
            // glTF importer wraps scene nodes below a root: find the named nodes in the tree
            var parent = find(root, "parent");
            assertNotNull(parent, "parent node must survive import");
            assertEquals(1, parent.mNumChildren());
            var child = AINode.create(parent.mChildren().get(0));
            assertEquals("child", child.mName().dataString());
            assertEquals(1, child.mNumMeshes());
        }
    }

    @Test
    void closeReleasesSceneAndBlocksFurtherAccess() {
        var imported = AssimpImporter.importScene(resource("/gltf/hierarchy.gltf"), AssimpFlags.DEFAULT);
        imported.close();
        imported.close(); // idempotent
        assertThrows(IllegalStateException.class, imported::scene);
    }

    @Test
    void missingFileThrowsTypedException() {
        var ex = assertThrows(AssimpImportException.class,
                () -> AssimpImporter.importScene("/does/not/exist.obj", AssimpFlags.DEFAULT));
        assertNotNull(ex.getMessage());
    }

    private static AINode find(AINode node, String name) {
        if (name.equals(node.mName().dataString())) {
            return node;
        }
        for (int i = 0; i < node.mNumChildren(); i++) {
            var found = find(AINode.create(node.mChildren().get(i)), name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
