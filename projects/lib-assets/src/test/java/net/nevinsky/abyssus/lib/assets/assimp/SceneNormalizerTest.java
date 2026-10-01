package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.nevinsky.abyssus.core.model.ModelData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SceneNormalizerTest {

    private static final float EPS = 1e-5f;

    private static void assertVector(float x, float y, float z, Vector3 actual) {
        assertEquals(x, actual.x, EPS, "x");
        assertEquals(y, actual.y, EPS, "y");
        assertEquals(z, actual.z, EPS, "z");
    }

    // ---------------------------------------------------------------------------------------------------------------
    // pure math
    // ---------------------------------------------------------------------------------------------------------------

    @Test
    void defaultYUpSystemNeedsNoCorrection() {
        assertNull(SceneNormalizer.axes(0, 1, 1, 1, 2, 1));
    }

    @Test
    void zUpIsRotatedToYUp() {
        // right X, up Z, towards the viewer -Y (what Blender writes for Z up)
        var m = SceneNormalizer.axes(0, 1, 2, 1, 1, -1);
        assertNotNull(m);

        assertVector(1, 0, 0, new Vector3(1, 0, 0).mul(m));
        assertVector(0, 1, 0, new Vector3(0, 0, 1).mul(m));
        assertVector(0, 0, 1, new Vector3(0, -1, 0).mul(m));
    }

    @Test
    void negativeUpAxisIsFlipped() {
        // up -Y: a point above (0, -1, 0) ends up at +Y; right X and the viewer direction follow from the handedness
        var m = SceneNormalizer.axes(0, 1, 1, -1, 2, -1);
        assertNotNull(m);

        assertVector(0, 1, 0, new Vector3(0, -1, 0).mul(m));
        assertVector(1, 0, 0, new Vector3(1, 0, 0).mul(m));
        assertVector(0, 0, 1, new Vector3(0, 0, -1).mul(m));
    }

    @Test
    void correctionIsAProperRotation() {
        var m = SceneNormalizer.axes(0, 1, 2, 1, 1, -1);
        assertEquals(1f, m.det(), EPS);
    }

    @Test
    void leftHandedOrInvalidAxesAreIgnored() {
        assertNull(SceneNormalizer.axes(0, 1, 1, 1, 2, -1), "left handed");
        assertNull(SceneNormalizer.axes(0, 1, 0, 1, 2, 1), "two equal axes");
        assertNull(SceneNormalizer.axes(0, 1, 3, 1, 2, 1), "axis out of range");
        assertNull(SceneNormalizer.axes(0, 0, 1, 1, 2, 1), "zero sign");
    }

    @Test
    void fbxUnitScaleFactorIsInCentimeters() {
        assertEquals(0.01f, SceneNormalizer.meterScale(1.0), EPS);
        assertEquals(1f, SceneNormalizer.meterScale(100.0), EPS);
        assertEquals(1f, SceneNormalizer.meterScale(0.0), EPS, "invalid factor is ignored");
    }

    @Test
    void applyMultipliesTheRootTransform() {
        var root = new ModelNode();
        root.translation = new Vector3(1, 2, 3);
        var rotateAndScale = new Matrix4().scl(2f);

        SceneNormalizer.apply(root, rotateAndScale);

        assertVector(2, 4, 6, root.translation);
        assertVector(2, 2, 2, root.scale);
        assertNull(root.rotation);
    }

    @Test
    void applyWithoutCorrectionKeepsTheNode() {
        var root = new ModelNode();
        root.rotation = new Quaternion().setFromAxis(0, 1, 0, 30);

        SceneNormalizer.apply(root, null);

        assertNotNull(root.rotation);
        assertNull(root.translation);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // fixtures: a triangle (0,0,0) (1,0,0) (0,1,0) in files with different axes and units
    // ---------------------------------------------------------------------------------------------------------------

    private static ModelData load(String name, boolean convertUnits) {
        var file = new File(Objects.requireNonNull(
                SceneNormalizerTest.class.getResource("/fbx/" + name + ".fbx")).getFile());
        return new AssimpModelDataLoader(convertUnits).load(name, new FileHandle(file));
    }

    /** The bounds of the triangle in the space of the scene, i.e. with the transforms of the nodes applied. */
    private static BoundingBox sceneBounds(ModelData data) {
        var bounds = new BoundingBox().inf();
        collectBounds(data, data.nodes.first(), new Matrix4(), bounds);
        return bounds;
    }

    private static void collectBounds(ModelData data, ModelNode node, Matrix4 parent, BoundingBox out) {
        var local = new Matrix4().set(node.translation != null ? node.translation : new Vector3(),
                node.rotation != null ? node.rotation : new Quaternion(),
                node.scale != null ? node.scale : new Vector3(1, 1, 1));
        var world = parent.cpy().mul(local);
        if (node.parts != null) {
            for (var nodePart : node.parts) {
                for (var mesh : data.meshes) {
                    for (var part : mesh.parts) {
                        if (part.id.equals(nodePart.meshPartId)) {
                            var bb = part.getBoundingBox();
                            for (var x : new float[]{bb.min.x, bb.max.x}) {
                                for (var y : new float[]{bb.min.y, bb.max.y}) {
                                    for (var z : new float[]{bb.min.z, bb.max.z}) {
                                        out.ext(new Vector3(x, y, z).mul(world));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (node.children != null) {
            for (var child : node.children) {
                collectBounds(data, child, world, out);
            }
        }
    }

    private static void assertBounds(Vector3 min, Vector3 max, BoundingBox actual) {
        assertVector(min.x, min.y, min.z, actual.min);
        assertVector(max.x, max.y, max.z, actual.max);
    }

    @Test
    void yUpFileIsNotChanged() {
        assertBounds(new Vector3(0, 0, 0), new Vector3(1, 1, 0), sceneBounds(load("yup_cm", false)));
    }

    @Test
    void zUpFileLiesInTheXzPlane() {
        // file (x, y, z) -> (x, z, -y): the triangle (0,0,0) (1,0,0) (0,1,0) becomes (0,0,0) (1,0,0) (0,0,-1)
        assertBounds(new Vector3(0, 0, -1), new Vector3(1, 0, 0), sceneBounds(load("zup_cm", false)));
    }

    @Test
    void unitsAreNotConvertedByDefault() {
        assertBounds(new Vector3(0, 0, 0), new Vector3(1, 1, 0), sceneBounds(load("yup_cm", false)));
    }

    @Test
    void centimetersAreConvertedToMetersOnRequest() {
        assertBounds(new Vector3(0, 0, 0), new Vector3(0.01f, 0.01f, 0), sceneBounds(load("yup_cm", true)));
    }

    @Test
    void fileInMetersStaysTheSameWhenConverting() {
        assertBounds(new Vector3(0, 0, 0), new Vector3(1, 1, 0), sceneBounds(load("yup_m", true)));
    }

    @Test
    void axesAndUnitsCombine() {
        assertBounds(new Vector3(0, 0, -0.01f), new Vector3(0.01f, 0, 0), sceneBounds(load("zup_cm", true)));
    }
}
