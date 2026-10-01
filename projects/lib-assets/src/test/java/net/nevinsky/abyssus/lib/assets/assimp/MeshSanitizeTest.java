package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector3;
import net.nevinsky.abyssus.core.model.ModelMesh;
import net.nevinsky.abyssus.core.model.ModelMeshPart;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeshSanitizeTest {

    // layout: position(3) normal(3) tangent(3) binormal(3)
    private static final int STRIDE = 12;

    private static ModelMesh mesh(float[][] vertices, int... indices) {
        var mesh = new ModelMesh();
        mesh.id = "m";
        mesh.attributes = new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.Normal(),
                VertexAttribute.Tangent(), VertexAttribute.Binormal()};
        mesh.vertices = new float[vertices.length * STRIDE];
        for (int i = 0; i < vertices.length; i++) {
            System.arraycopy(vertices[i], 0, mesh.vertices, i * STRIDE, STRIDE);
        }
        var part = new ModelMeshPart();
        part.id = "p";
        part.indices = indices;
        mesh.parts = new ModelMeshPart[]{part};
        return mesh;
    }

    private static float[] vertex(float x, float y, float z, float[] normal, float[] tangent, float[] binormal) {
        return new float[]{x, y, z, normal[0], normal[1], normal[2], tangent[0], tangent[1], tangent[2],
                binormal[0], binormal[1], binormal[2]};
    }

    private static Vector3 normalOf(ModelMesh mesh, int vertex) {
        int o = vertex * STRIDE + 3;
        return new Vector3(mesh.vertices[o], mesh.vertices[o + 1], mesh.vertices[o + 2]);
    }

    private static Vector3 tangentOf(ModelMesh mesh, int vertex) {
        int o = vertex * STRIDE + 6;
        return new Vector3(mesh.vertices[o], mesh.vertices[o + 1], mesh.vertices[o + 2]);
    }

    private static Vector3 binormalOf(ModelMesh mesh, int vertex) {
        int o = vertex * STRIDE + 9;
        return new Vector3(mesh.vertices[o], mesh.vertices[o + 1], mesh.vertices[o + 2]);
    }

    private static final float[] Z = {0, 0, 0};
    private static final float[] UP = {0, 0, 1};
    private static final float[] X = {1, 0, 0};
    private static final float[] Y = {0, 1, 0};

    @Test
    void zeroNormalOfAValidTriangleBecomesTheFaceNormal() {
        // triangle in the XY plane, counter clockwise: face normal +Z; the last vertex has a zero normal
        var mesh = mesh(new float[][]{
                vertex(0, 0, 0, UP, X, Y), vertex(1, 0, 0, UP, X, Y), vertex(0, 1, 0, Z, X, Y)}, 0, 1, 2);

        MeshProcessor.sanitizeDirections(mesh);

        var n = normalOf(mesh, 2);
        assertEquals(0f, n.x, 1e-5f);
        assertEquals(0f, n.y, 1e-5f);
        assertEquals(1f, n.z, 1e-5f);
    }

    @Test
    void zeroNormalOfOnlyDegenerateTrianglesBecomesUp() {
        // the three vertices are on one line: the triangle has no normal
        var mesh = mesh(new float[][]{
                vertex(0, 0, 0, Z, X, Y), vertex(1, 0, 0, Z, X, Y), vertex(2, 0, 0, Z, X, Y)}, 0, 1, 2);

        MeshProcessor.sanitizeDirections(mesh);

        for (int i = 0; i < 3; i++) {
            assertEquals(new Vector3(0, 1, 0), normalOf(mesh, i));
        }
    }

    @Test
    void zeroTangentAndBinormalBecomePerpendicularUnitVectors() {
        var mesh = mesh(new float[][]{
                vertex(0, 0, 0, UP, Z, Z), vertex(1, 0, 0, UP, Z, Z), vertex(0, 1, 0, UP, Z, Z)}, 0, 1, 2);

        MeshProcessor.sanitizeDirections(mesh);

        for (int i = 0; i < 3; i++) {
            var n = normalOf(mesh, i);
            var t = tangentOf(mesh, i);
            var b = binormalOf(mesh, i);
            assertEquals(1f, t.len(), 1e-5f);
            assertEquals(1f, b.len(), 1e-5f);
            assertEquals(0f, n.dot(t), 1e-5f);
            assertEquals(0f, n.dot(b), 1e-5f);
            assertEquals(0f, t.dot(b), 1e-5f);
        }
    }

    @Test
    void validDataIsNotTouched() {
        var mesh = mesh(new float[][]{
                vertex(0, 0, 0, UP, X, Y), vertex(1, 0, 0, UP, X, Y), vertex(0, 1, 0, UP, X, Y)}, 0, 1, 2);
        var before = mesh.vertices.clone();

        MeshProcessor.sanitizeDirections(mesh);

        org.junit.jupiter.api.Assertions.assertArrayEquals(before, mesh.vertices, 0f);
    }

    @Test
    void meshWithoutNormalsIsIgnored() {
        var mesh = new ModelMesh();
        mesh.attributes = new VertexAttribute[]{VertexAttribute.Position()};
        mesh.vertices = new float[]{0, 0, 0, 1, 0, 0, 0, 1, 0};
        mesh.parts = new ModelMeshPart[0];

        MeshProcessor.sanitizeDirections(mesh);

        assertEquals(9, mesh.vertices.length);
    }
}
