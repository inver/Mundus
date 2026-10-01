package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.nevinsky.abyssus.core.model.ModelData;
import net.nevinsky.abyssus.core.model.ModelMesh;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Loads the real models from the {@code samples} folder and checks them against values read directly from the model
 * files (see {@link SampleOracles}). The tests are skipped if the folder is missing.
 */
class AssimpSamplesTest {

    private static final float EPS = 1e-4f;

    private static Path samples;

    @BeforeAll
    static void findSamples() {
        var dir = System.getProperty("samples.dir");
        assumeTrue(dir != null && Files.isDirectory(Path.of(dir)), "samples folder not found");
        samples = Path.of(dir);
    }

    private static ModelData load(Path file) {
        return new AssimpModelDataLoader().load(file.getFileName().toString(), new FileHandle(file.toFile()));
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Checks that every loaded model has to pass
    // ---------------------------------------------------------------------------------------------------------------

    private static int stride(ModelMesh mesh) {
        int stride = 0;
        for (var attribute : mesh.attributes) {
            stride += attribute.numComponents;
        }
        return stride;
    }

    private static int triangles(ModelData data) {
        int sum = 0;
        for (var mesh : data.meshes) {
            for (var part : mesh.parts) {
                sum += part.indices.length / 3;
            }
        }
        return sum;
    }

    private static List<ModelNode> allNodes(ModelData data) {
        var res = new ArrayList<ModelNode>();
        for (var root : data.nodes) {
            collect(root, res);
        }
        return res;
    }

    private static void collect(ModelNode node, List<ModelNode> out) {
        out.add(node);
        if (node.children != null) {
            for (var child : node.children) {
                collect(child, out);
            }
        }
    }

    private static ModelNode findNode(ModelData data, String id) {
        return allNodes(data).stream().filter(n -> id.equals(n.id)).findFirst().orElse(null);
    }

    private static void assertConsistent(ModelData data) {
        assertFalse(data.meshes.isEmpty(), "no meshes");

        var partIds = new HashSet<String>();
        for (var mesh : data.meshes) {
            int stride = stride(mesh);
            assertEquals(0, mesh.vertices.length % stride, "vertex data is not a multiple of the stride");
            int vertexCount = mesh.vertices.length / stride;
            assertTrue(vertexCount > 0, "empty mesh " + mesh.id);

            for (var f : mesh.vertices) {
                assertTrue(Float.isFinite(f), "NaN or infinite vertex data in " + mesh.id);
            }
            for (var part : mesh.parts) {
                assertTrue(partIds.add(part.id), "duplicate mesh part id " + part.id);
                assertEquals(0, part.indices.length % 3, "indices of " + part.id + " are not triangles");
                assertTrue(part.indices.length > 0, "empty part " + part.id);
                for (int index : part.indices) {
                    assertTrue(index >= 0 && index < vertexCount, "index " + index + " out of range in " + mesh.id);
                }
                var bb = part.getBoundingBox();
                assertNotNull(bb);
                assertTrue(bb.min.x <= bb.max.x && bb.min.y <= bb.max.y && bb.min.z <= bb.max.z,
                        "inverted bounding box in " + part.id);
            }
            checkNormalsAreUnit(mesh, stride);
        }

        var materialIds = new HashSet<String>();
        for (var material : data.materials) {
            assertTrue(materialIds.add(material.id), "duplicate material id " + material.id);
        }

        var nodeIds = new HashSet<String>();
        for (var node : allNodes(data)) {
            assertNotNull(node.id);
            assertTrue(nodeIds.add(node.id), "duplicate node id " + node.id);
            if (node.parts != null) {
                for (var part : node.parts) {
                    assertTrue(partIds.contains(part.meshPartId), "unknown mesh part " + part.meshPartId);
                    assertTrue(materialIds.contains(part.materialId), "unknown material " + part.materialId);
                }
            }
        }

        for (var material : data.materials) {
            for (var texture : material.textures) {
                assertTrue(new File(texture.fileName).isFile(),
                        "texture of " + material.id + " does not exist: " + texture.fileName);
            }
        }
    }

    private static void checkNormalsAreUnit(ModelMesh mesh, int stride) {
        int offset = 0;
        boolean found = false;
        for (var attribute : mesh.attributes) {
            if (attribute.usage == Usage.Normal) {
                found = true;
                break;
            }
            offset += attribute.numComponents;
        }
        assertTrue(found, "mesh " + mesh.id + " has no normals");
        int count = mesh.vertices.length / stride;
        int step = Math.max(1, count / 500);
        for (int i = 0; i < count; i += step) {
            int base = i * stride + offset;
            float len = new Vector3(mesh.vertices[base], mesh.vertices[base + 1], mesh.vertices[base + 2]).len();
            assertEquals(1f, len, 1e-2f, "normal is not a unit vector in " + mesh.id);
        }
    }

    private static BoundingBox boundsOfParts(ModelData data) {
        var bounds = new BoundingBox().inf();
        for (var mesh : data.meshes) {
            for (var part : mesh.parts) {
                bounds.ext(part.getBoundingBox());
            }
        }
        return bounds;
    }

    private static void assertBounds(BoundingBox expected, BoundingBox actual, float tolerance) {
        assertEquals(expected.min.x, actual.min.x, tolerance, "min.x");
        assertEquals(expected.min.y, actual.min.y, tolerance, "min.y");
        assertEquals(expected.min.z, actual.min.z, tolerance, "min.z");
        assertEquals(expected.max.x, actual.max.x, tolerance, "max.x");
        assertEquals(expected.max.y, actual.max.y, tolerance, "max.y");
        assertEquals(expected.max.z, actual.max.z, tolerance, "max.z");
    }

    // ---------------------------------------------------------------------------------------------------------------

    @Nested
    class Piper {
        private ModelData data;
        private Path dir;

        private ModelData data() {
            if (data == null) {
                dir = samples.resolve("piper");
                data = AssimpSamplesTest.load(dir.resolve("piper_pa18.obj"));
            }
            return data;
        }

        @Test
        void isConsistent() {
            assertConsistent(data());
        }

        @Test
        void triangleCountMatchesTheObjFile() {
            var oracle = SampleOracles.parseObj(samples.resolve("piper/piper_pa18.obj"));
            assertEquals(oracle.triangles, triangles(data()));
        }

        @Test
        void boundsMatchTheObjVertices() {
            var oracle = SampleOracles.parseObj(samples.resolve("piper/piper_pa18.obj"));
            assertBounds(oracle.bounds, boundsOfParts(data()), EPS);
        }

        @Test
        void materialsKeepTheirNames() {
            var oracle = SampleOracles.parseObj(samples.resolve("piper/piper_pa18.obj"));
            var ids = new HashSet<String>();
            data().materials.forEach(m -> ids.add(m.id));
            assertTrue(ids.containsAll(oracle.materials), "missing " + oracle.materials + " in " + ids);
        }

        @Test
        void texturesWithBackslashPathsAreResolved() {
            // the mtl uses "textures\\piper_diffuse.jpg" and "textures\\\\piper_bump.jpg"
            var base = data().materials.select(m -> m.id.equals("base")).iterator().next();
            var files = new HashSet<String>();
            base.textures.forEach(t -> files.add(new File(t.fileName).getName()));
            assertEquals(Set.of("piper_diffuse.jpg", "piper_bump.jpg"), files);
            base.textures.forEach(t -> assertTrue(t.fileName.contains("/textures/"), t.fileName));
        }

        @Test
        void materialColorsAndShininessAreRead() {
            var base = data().materials.select(m -> m.id.equals("base")).iterator().next();
            assertNotNull(base.diffuse);
            assertEquals(0.683972f, base.diffuse.r, EPS);
            assertEquals(0.355875f, base.diffuse.g, EPS);
            assertNotNull(base.specular);
            assertEquals(0.5f, base.specular.r, EPS);
            assertEquals(96.078431f, base.shininess, 1e-2f);
        }
    }

    @Nested
    class Cessna {
        private ModelData data;

        private ModelData data() {
            if (data == null) {
                data = AssimpSamplesTest.load(samples.resolve("cessna-172/model.obj"));
            }
            return data;
        }

        @Test
        void isConsistent() {
            assertConsistent(data());
        }

        @Test
        void triangleCountMatchesTheObjFile() {
            var oracle = SampleOracles.parseObj(samples.resolve("cessna-172/model.obj"));
            assertEquals(oracle.triangles, triangles(data()));
        }

        @Test
        void boundsMatchTheObjVertices() {
            var oracle = SampleOracles.parseObj(samples.resolve("cessna-172/model.obj"));
            assertBounds(oracle.bounds, boundsOfParts(data()), EPS);
        }

        @Test
        void everyObjGroupBecomesANode() {
            var oracle = SampleOracles.parseObj(samples.resolve("cessna-172/model.obj"));
            assertFalse(oracle.groups.isEmpty());
            for (var group : oracle.groups) {
                assertNotNull(findNode(data(), group), "no node for group " + group);
            }
        }

        @Test
        void textureOfFirstMaterialIsResolved() {
            var mat = data().materials.select(m -> m.id.equals("mat00")).iterator().next();
            assertEquals(1, mat.textures.size, "empty map_bump/map_d/... lines must not create textures");
            assertEquals("c172_t.tga", new File(mat.textures.first().fileName).getName());
        }

        @Test
        void materialWithoutTextureHasNone() {
            var mat = data().materials.select(m -> m.id.equals("mat01")).iterator().next();
            assertEquals(0, mat.textures.size);
            assertNotNull(mat.diffuse);
        }

        @Test
        void shininessIsTakenFromTheMtl() {
            var mat = data().materials.select(m -> m.id.equals("mat00")).iterator().next();
            assertEquals(29.8571f, mat.shininess, 1e-2f);
        }

        @Test
        void exportAndReloadKeepsTheGeometry(@TempDir Path tmp) {
            var out = new FileHandle(tmp.toFile());
            new AssimpModelExporter().exportGltf(new FileHandle(samples.resolve("cessna-172/model.obj").toFile()),
                    out.child("model.gltf"));

            assertTrue(out.child("c172_t.tga").exists(), "texture must be copied next to the model");
            var reloaded = AssimpSamplesTest.load(tmp.resolve("model.gltf"));
            assertEquals(triangles(data()), triangles(reloaded));
            assertEquals(data().materials.size, reloaded.materials.size);
        }
    }

    @Nested
    class Sr22 {
        private ModelData data;
        private SampleOracles.AcStats oracle;

        private ModelData data() {
            if (data == null) {
                data = AssimpSamplesTest.load(samples.resolve("Cirrus-SR22/Models/sr22.ac"));
                oracle = SampleOracles.parseAc(samples.resolve("Cirrus-SR22/Models/sr22.ac"));
            }
            return data;
        }

        @Test
        void isConsistent() {
            assertConsistent(data());
        }

        @Test
        void triangleCountMatchesTheAcFile() {
            data();
            assertEquals(oracle.triangles(), triangles(data()));
        }

        @Test
        void everyNamedObjectBecomesANode() {
            data();
            var polys = oracle.polys();
            assertEquals(37, polys.size());
            for (var name : polys.keySet()) {
                assertNotNull(findNode(data(), name), "no node for object " + name);
            }
        }

        /**
         * Assimp applies the {@code loc} of AC3D objects to the vertices, so the node transforms stay identity and the
         * bounds of an object are its raw bounds moved by loc.
         */
        @Test
        void objectLocationIsAppliedToTheGeometry() {
            data();
            int checked = 0;
            for (var entry : oracle.polys().entrySet()) {
                var object = entry.getValue();
                var node = findNode(data(), entry.getKey());
                if (object.rotated || object.usedVertices.isEmpty() || node.parts == null) {
                    continue;
                }
                var bounds = new BoundingBox().inf();
                for (var nodePart : node.parts) {
                    for (var mesh : data().meshes) {
                        for (var part : mesh.parts) {
                            if (part.id.equals(nodePart.meshPartId)) {
                                bounds.ext(part.getBoundingBox());
                            }
                        }
                    }
                }
                assertBounds(object.boundsWithLoc(), bounds, 1e-3f);
                checked++;
            }
            assertTrue(checked > 20, "only " + checked + " objects were checked");
        }

        @Test
        void hierarchyIsKept() {
            data();
            var root = data().nodes.first();
            assertTrue(root.id.startsWith("ACWorld"), "the AC3D world object is the root, but was " + root.id);
            assertEquals(37, root.children.length, "kids 37");
        }

        @Test
        void texturesAreResolvedNextToTheModel() {
            var names = new HashSet<String>();
            for (var material : data().materials) {
                material.textures.forEach(t -> names.add(new File(t.fileName).getName()));
            }
            assertTrue(names.contains("texture.png"), names.toString());
            assertTrue(names.contains("prop.png"), names.toString());
        }

        @Test
        void allAc3dFilesOfThePackageLoad() throws Exception {
            try (var stream = Files.walk(samples.resolve("Cirrus-SR22"))) {
                var files = stream.filter(p -> p.toString().endsWith(".ac")).sorted().toArray(Path[]::new);
                assertTrue(files.length > 20, "expected the .ac files of the package");
                for (var file : files) {
                    var stats = SampleOracles.parseAc(file);
                    ModelData model;
                    try {
                        model = AssimpSamplesTest.load(file);
                    } catch (AssimpImportException e) {
                        assertEquals(0, stats.triangles(), file + " failed to load: " + e.getMessage());
                        continue;
                    }
                    assertEquals(stats.triangles(), triangles(model), "triangles of " + file);
                    if (!model.meshes.isEmpty()) {
                        assertConsistent(model);
                    }
                }
            }
        }
    }
}
