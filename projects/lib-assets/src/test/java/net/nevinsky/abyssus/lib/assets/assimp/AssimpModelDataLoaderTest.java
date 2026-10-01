package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssimpModelDataLoaderTest {

    private static net.nevinsky.abyssus.core.model.ModelData data;

    @BeforeAll
    static void load() {
        var file = new File(Objects.requireNonNull(
                AssimpModelDataLoaderTest.class.getResource("/gltf/hierarchy.gltf")).getFile());
        data = new AssimpModelDataLoader().load("hierarchy", new FileHandle(file));
    }

    @Test
    void nodeTreeAndTransformsArePreserved() {
        var parent = find(data.nodes.first(), "parent");
        assertNotNull(parent);
        assertEquals(1f, parent.translation.x, 1e-5f);
        assertEquals(2f, parent.translation.y, 1e-5f);
        assertEquals(3f, parent.translation.z, 1e-5f);
        assertNull(parent.parts, "parent has no mesh");

        var child = parent.children[0];
        assertEquals("child", child.id);
        assertEquals(2f, child.scale.x, 1e-5f);
        assertEquals(1, child.parts.length);
        assertNotNull(child.meshId);
    }

    @Test
    void nodePartPointsToExistingMeshPartAndMaterial() {
        var child = find(data.nodes.first(), "child");
        var part = child.parts[0];
        assertTrue(data.meshes.first().parts[0].id.equals(part.meshPartId));
        int matches = 0;
        for (var material : data.materials) {
            if (material.id.equals(part.materialId)) {
                matches++;
            }
        }
        assertEquals(1, matches);
    }

    @Test
    void meshHasPositionsAndTriangleIndices() {
        var mesh = data.meshes.first();
        assertEquals(Usage.Position, mesh.attributes[0].usage);
        assertArrayEquals(new int[]{0, 1, 2}, sorted(mesh.parts[0].indices));
        var bb = mesh.parts[0].getBoundingBox();
        assertEquals(1f, bb.max.x, 1e-5f);
        assertEquals(1f, bb.max.y, 1e-5f);
    }

    @Test
    void materialKeepsNameAndColor() {
        var material = data.materials.first();
        assertEquals("red", material.id);
        assertNotNull(material.diffuse);
        assertEquals(1f, material.diffuse.r, 1e-5f);
        assertEquals(0f, material.diffuse.g, 1e-5f);
    }

    @Test
    void missingFileThrows() {
        assertThrows(AssimpImportException.class,
                () -> new AssimpModelDataLoader().load("x", new FileHandle("/does/not/exist.obj")));
    }

    @Test
    void normalizePathKeepsSubfolders() {
        assertEquals("textures/a.png", TextureProcessor.normalizePath("textures\\a.png"));
        assertEquals("textures/a.png", TextureProcessor.normalizePath("./textures//a.png"));
    }

    private static int[] sorted(int[] a) {
        var c = a.clone();
        java.util.Arrays.sort(c);
        return c;
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
}
