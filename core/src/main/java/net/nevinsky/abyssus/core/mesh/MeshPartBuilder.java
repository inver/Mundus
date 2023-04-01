package net.nevinsky.abyssus.core.mesh;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public interface MeshPartBuilder {
    /**
     * @return The {@link MeshPart} currently building.
     */
    MeshPart getMeshPart();

    /**
     * @return The primitive type used for building, e.g. {@link GL20#GL_TRIANGLES} or {@link GL20#GL_LINES}.
     */
    int getPrimitiveType();

    /**
     * @return The {@link VertexAttributes} available for building.
     */
    VertexAttributes getAttributes();

    /**
     * Set the color used to tint the vertex color, defaults to white. Only applicable for
     * {@link VertexAttributes.Usage#ColorPacked} or {@link VertexAttributes.Usage#ColorUnpacked}.
     */
    void setColor(final Color color);

    /**
     * Set the color used to tint the vertex color, defaults to white. Only applicable for
     * {@link VertexAttributes.Usage#ColorPacked} or {@link VertexAttributes.Usage#ColorUnpacked}.
     */
    void setColor(float r, float g, float b, float a);

    /**
     * Set range of texture coordinates used (default is 0,0,1,1).
     */
    void setUVRange(float u1, float v1, float u2, float v2);

    /**
     * Set range of texture coordinates from the specified TextureRegion.
     */
    void setUVRange(TextureRegion r);

    /**
     * Get the current vertex transformation matrix.
     */
    Matrix4 getVertexTransform(Matrix4 out);

    /**
     * Set the current vertex transformation matrix and enables vertex transformation.
     */
    void setVertexTransform(Matrix4 transform);

    /**
     * Indicates whether vertex transformation is enabled.
     */
    boolean isVertexTransformationEnabled();

    /**
     * Sets whether vertex transformation is enabled.
     */
    void setVertexTransformationEnabled(boolean enabled);

    /**
     * Increases the size of the backing vertices array to accommodate the specified number of additional vertices.
     * Useful before adding many vertices to avoid multiple backing array resizes.
     *
     * @param numVertices The number of vertices you are about to add
     */
    void ensureVertices(int numVertices);

    /**
     * Increases the size of the backing indices array to accommodate the specified number of additional indices. Useful
     * before adding many indices to avoid multiple backing array resizes.
     *
     * @param numIndices The number of indices you are about to add
     */
    void ensureIndices(int numIndices);

    /**
     * Increases the size of the backing vertices and indices arrays to accommodate the specified number of additional
     * vertices and indices. Useful before adding many vertices and indices to avoid multiple backing array resizes.
     *
     * @param numVertices The number of vertices you are about to add
     * @param numIndices  The number of indices you are about to add
     */
    void ensureCapacity(int numVertices, int numIndices);

    /**
     * Increases the size of the backing indices array to accommodate the specified number of additional triangles.
     * Useful before adding many triangles using {@link #triangle(int, int, int)} to avoid multiple backing array
     * resizes. The actual number of indices accounted for depends on the primitive type (see
     * {@link #getPrimitiveType()}).
     *
     * @param numTriangles The number of triangles you are about to add
     */
    void ensureTriangleIndices(int numTriangles);

    /**
     * Increases the size of the backing indices array to accommodate the specified number of additional rectangles.
     * Useful before adding many rectangles using {@link #rect(int, int, int, int)} to avoid multiple backing array
     * resizes.
     *
     * @param numRectangles The number of rectangles you are about to add
     */
    void ensureRectangleIndices(int numRectangles);

    /**
     * Add one or more vertices, returns the index of the last vertex added. The length of values must a power of the
     * vertex size.
     */
    int vertex(final float... values);

    /**
     * Add a vertex, returns the index. Null values are allowed. Use {@link #getAttributes} to check which values are
     * available.
     */
    int vertex(Vector3 pos, Vector3 nor, Color col, Vector2 uv);

    /**
     * Add a vertex, returns the index. Use {@link #getAttributes} to check which values are available.
     */
    int vertex(final MeshPartBuilder.VertexInfo info);

    /**
     * @return The index of the last added vertex.
     */
    int lastIndex();

    /**
     * Add an index, MeshPartBuilder expects all meshes to be indexed.
     */
    void index(final int value);

    /**
     * Add multiple indices, MeshPartBuilder expects all meshes to be indexed.
     */
    void index(int value1, int value2);

    /**
     * Add multiple indices, MeshPartBuilder expects all meshes to be indexed.
     */
    void index(int value1, int value2, int value3);

    /**
     * Add multiple indices, MeshPartBuilder expects all meshes to be indexed.
     */
    void index(int value1, int value2, int value3, int value4);

    /**
     * Add multiple indices, MeshPartBuilder expects all meshes to be indexed.
     */
    void index(int value1, int value2, int value3, int value4, int value5, int value6);

    /**
     * Add multiple indices, MeshPartBuilder expects all meshes to be indexed.
     */
    void index(int value1, int value2, int value3, int value4, int value5, int value6, int value7,
               int value8);

    /**
     * Add a line by indices. Requires GL_LINES primitive type.
     */
    void line(int index1, int index2);

    /**
     * Add a line. Requires GL_LINES primitive type.
     */
    void line(MeshPartBuilder.VertexInfo p1,
              MeshPartBuilder.VertexInfo p2);

    /**
     * Add a line. Requires GL_LINES primitive type.
     */
    void line(Vector3 p1, Vector3 p2);

    /**
     * Add a line. Requires GL_LINES primitive type.
     */
    void line(float x1, float y1, float z1, float x2, float y2, float z2);

    /**
     * Add a line. Requires GL_LINES primitive type.
     */
    void line(Vector3 p1, Color c1, Vector3 p2, Color c2);

    /**
     * Add a triangle by indices. Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void triangle(int index1, int index2, int index3);

    /**
     * Add a triangle. Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void triangle(MeshPartBuilder.VertexInfo p1,
                  MeshPartBuilder.VertexInfo p2,
                  MeshPartBuilder.VertexInfo p3);

    /**
     * Add a triangle. Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void triangle(Vector3 p1, Vector3 p2, Vector3 p3);

    /**
     * Add a triangle. Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void triangle(Vector3 p1, Color c1, Vector3 p2, Color c2, Vector3 p3, Color c3);

    /**
     * Add a rectangle by indices. Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void rect(int corner00, int corner10, int corner11, int corner01);

    /**
     * Add a rectangle. Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void rect(MeshPartBuilder.VertexInfo corner00,
              MeshPartBuilder.VertexInfo corner10,
              MeshPartBuilder.VertexInfo corner11,
              MeshPartBuilder.VertexInfo corner01);

    /**
     * Add a rectangle. Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void rect(Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, Vector3 normal);

    /**
     * Add a rectangle Requires GL_POINTS, GL_LINES or GL_TRIANGLES primitive type.
     */
    void rect(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11,
              float x01,
              float y01, float z01, float normalX, float normalY, float normalZ);

    /**
     * Copies a mesh to the mesh (part) currently being build.
     *
     * @param mesh The mesh to copy, must have the same vertex attributes and must be indexed.
     */
    void addMesh(Mesh mesh);

    /**
     * Copies a MeshPart to the mesh (part) currently being build.
     *
     * @param meshpart The MeshPart to copy, must have the same vertex attributes, primitive type and must be indexed.
     */
    void addMesh(MeshPart meshpart);

    /**
     * Copies a (part of a) mesh to the mesh (part) currently being build.
     *
     * @param mesh        The mesh to (partly) copy, must have the same vertex attributes and must be indexed.
     * @param indexOffset The zero-based offset of the first index of the part of the mesh to copy.
     * @param numIndices  The number of indices of the part of the mesh to copy.
     */
    void addMesh(Mesh mesh, int indexOffset, int numIndices);

    /**
     * Copies a mesh to the mesh (part) currently being build. The entire vertices array is added, even if some of the
     * vertices are not indexed by the indices array. If you want to add only the vertices that are actually indexed,
     * then use the {@link #addMesh(float[], int[], int, int)} method instead.
     *
     * @param vertices The vertices to copy, must be in the same vertex layout as the mesh being build.
     * @param indices  Array containing the indices to copy, each index should be valid in the vertices array.
     */
    void addMesh(float[] vertices, int[] indices);

    /**
     * Copies a (part of a) mesh to the mesh (part) currently being build.
     *
     * @param vertices    The vertices to (partly) copy, must be in the same vertex layout as the mesh being build.
     * @param indices     Array containing the indices to (partly) copy, each index should be valid in the vertices
     *                    array.
     * @param indexOffset The zero-based offset of the first index of the part of indices array to copy.
     * @param numIndices  The number of indices of the part of the indices array to copy.
     */
    void addMesh(float[] vertices, int[] indices, int indexOffset, int numIndices);

    /**
     * Class that contains all vertex information the builder can use.
     *
     * @author Xoppa
     */
    class VertexInfo implements Pool.Poolable {
        public final Vector3 position = new Vector3();
        public boolean hasPosition;
        public final Vector3 normal = new Vector3(0, 1, 0);
        public boolean hasNormal;
        public final Color color = new Color(1, 1, 1, 1);
        public boolean hasColor;
        public final Vector2 uv = new Vector2();
        public boolean hasUV;

        @Override
        public void reset() {
            position.set(0, 0, 0);
            normal.set(0, 1, 0);
            color.set(1, 1, 1, 1);
            uv.set(0, 0);
        }

        public MeshPartBuilder.VertexInfo set(Vector3 pos, Vector3 nor, Color col,
                                              Vector2 uv) {
            reset();
            hasPosition = pos != null;
            if (hasPosition) position.set(pos);
            hasNormal = nor != null;
            if (hasNormal) normal.set(nor);
            hasColor = col != null;
            if (hasColor) color.set(col);
            hasUV = uv != null;
            if (hasUV) this.uv.set(uv);
            return this;
        }

        public MeshPartBuilder.VertexInfo set(
                final MeshPartBuilder.VertexInfo other) {
            if (other == null) return set(null, null, null, null);
            hasPosition = other.hasPosition;
            position.set(other.position);
            hasNormal = other.hasNormal;
            normal.set(other.normal);
            hasColor = other.hasColor;
            color.set(other.color);
            hasUV = other.hasUV;
            uv.set(other.uv);
            return this;
        }

        public MeshPartBuilder.VertexInfo setPos(float x, float y, float z) {
            position.set(x, y, z);
            hasPosition = true;
            return this;
        }

        public MeshPartBuilder.VertexInfo setPos(Vector3 pos) {
            hasPosition = pos != null;
            if (hasPosition) position.set(pos);
            return this;
        }

        public MeshPartBuilder.VertexInfo setNor(float x, float y, float z) {
            normal.set(x, y, z);
            hasNormal = true;
            return this;
        }

        public MeshPartBuilder.VertexInfo setNor(Vector3 nor) {
            hasNormal = nor != null;
            if (hasNormal) normal.set(nor);
            return this;
        }

        public MeshPartBuilder.VertexInfo setCol(float r, float g, float b,
                                                 float a) {
            color.set(r, g, b, a);
            hasColor = true;
            return this;
        }

        public MeshPartBuilder.VertexInfo setCol(Color col) {
            hasColor = col != null;
            if (hasColor) color.set(col);
            return this;
        }

        public MeshPartBuilder.VertexInfo setUV(float u, float v) {
            uv.set(u, v);
            hasUV = true;
            return this;
        }

        public MeshPartBuilder.VertexInfo setUV(Vector2 uv) {
            hasUV = uv != null;
            if (hasUV) this.uv.set(uv);
            return this;
        }

        public MeshPartBuilder.VertexInfo lerp(
                final MeshPartBuilder.VertexInfo target, float alpha) {
            if (hasPosition && target.hasPosition) position.lerp(target.position, alpha);
            if (hasNormal && target.hasNormal) normal.lerp(target.normal, alpha);
            if (hasColor && target.hasColor) color.lerp(target.color, alpha);
            if (hasUV && target.hasUV) uv.lerp(target.uv, alpha);
            return this;
        }
    }

    // TODO: The following methods are deprecated and will be removed in a future release

    /**
     * @deprecated use PatchShapeBuilder.build instead.
     */
    @Deprecated
    void patch(MeshPartBuilder.VertexInfo corner00,
               MeshPartBuilder.VertexInfo corner10,
               MeshPartBuilder.VertexInfo corner11,
               MeshPartBuilder.VertexInfo corner01, int divisionsU,
               int divisionsV);

    /**
     * @deprecated use PatchShapeBuilder.build instead.
     */
    @Deprecated
    void patch(Vector3 corner00, Vector3 corner10, Vector3 corner11, Vector3 corner01, Vector3 normal,
               int divisionsU,
               int divisionsV);

    /**
     * @deprecated use PatchShapeBuilder.build instead.
     */
    @Deprecated
    void patch(float x00, float y00, float z00, float x10, float y10, float z10, float x11, float y11, float z11,
               float x01, float y01, float z01, float normalX, float normalY, float normalZ, int divisionsU,
               int divisionsV);

    /**
     * @deprecated use BoxShapeBuilder.build instead.
     */
    @Deprecated
    void box(MeshPartBuilder.VertexInfo corner000,
             MeshPartBuilder.VertexInfo corner010,
             MeshPartBuilder.VertexInfo corner100,
             MeshPartBuilder.VertexInfo corner110,
             MeshPartBuilder.VertexInfo corner001,
             MeshPartBuilder.VertexInfo corner011,
             MeshPartBuilder.VertexInfo corner101,
             MeshPartBuilder.VertexInfo corner111);

    /**
     * @deprecated use BoxShapeBuilder.build instead.
     */
    @Deprecated
    void box(Vector3 corner000, Vector3 corner010, Vector3 corner100, Vector3 corner110, Vector3 corner001,
             Vector3 corner011, Vector3 corner101, Vector3 corner111);

    /**
     * @deprecated use BoxShapeBuilder.build instead.
     */
    @Deprecated
    void box(Matrix4 transform);

    /**
     * @deprecated use BoxShapeBuilder.build instead.
     */
    @Deprecated
    void box(float width, float height, float depth);

    /**
     * @deprecated use BoxShapeBuilder.build instead.
     */
    @Deprecated
    void box(float x, float y, float z, float width, float height, float depth);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX,
                float normalY,
                float normalZ);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, final Vector3 center, final Vector3 normal);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent,
                final Vector3 binormal);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX,
                float normalY,
                float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY,
                float binormalZ);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX,
                float normalY,
                float normalZ, float angleFrom, float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, final Vector3 center, final Vector3 normal, float angleFrom,
                float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, final Vector3 center, final Vector3 normal, final Vector3 tangent,
                final Vector3 binormal, float angleFrom, float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void circle(float radius, int divisions, float centerX, float centerY, float centerZ, float normalX,
                float normalY,
                float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX, float binormalY,
                float binormalZ,
                float angleFrom, float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ,
                 float normalX,
                 float normalY, float normalZ);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, final Vector3 center, final Vector3 normal);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, final Vector3 center, final Vector3 normal,
                 final Vector3 tangent, final Vector3 binormal);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ,
                 float normalX,
                 float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX,
                 float binormalY,
                 float binormalZ);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ,
                 float normalX,
                 float normalY, float normalZ, float angleFrom, float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, final Vector3 center, final Vector3 normal,
                 float angleFrom,
                 float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, final Vector3 center, final Vector3 normal,
                 final Vector3 tangent, final Vector3 binormal, float angleFrom, float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, int divisions, float centerX, float centerY, float centerZ,
                 float normalX,
                 float normalY, float normalZ, float tangentX, float tangentY, float tangentZ, float binormalX,
                 float binormalY,
                 float binormalZ, float angleFrom, float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX,
                 float centerY, float centerZ, float normalX, float normalY, float normalZ, float tangentX,
                 float tangentY, float tangentZ,
                 float binormalX, float binormalY, float binormalZ, float angleFrom, float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX,
                 float centerY, float centerZ, float normalX, float normalY, float normalZ, float angleFrom,
                 float angleTo);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, float centerX,
                 float centerY, float centerZ, float normalX, float normalY, float normalZ);

    /**
     * @deprecated Use EllipseShapeBuilder.build instead.
     */
    @Deprecated
    void ellipse(float width, float height, float innerWidth, float innerHeight, int divisions, Vector3 center,
                 Vector3 normal);

    /**
     * @deprecated Use CylinderShapeBuilder.build instead.
     */
    @Deprecated
    void cylinder(float width, float height, float depth, int divisions);

    /**
     * @deprecated Use CylinderShapeBuilder.build instead.
     */
    @Deprecated
    void cylinder(float width, float height, float depth, int divisions, float angleFrom, float angleTo);

    /**
     * @deprecated Use CylinderShapeBuilder.build instead.
     */
    @Deprecated
    void cylinder(float width, float height, float depth, int divisions, float angleFrom, float angleTo,
                  boolean close);

    /**
     * @deprecated Use ConeShapeBuilder.build instead.
     */
    @Deprecated
    void cone(float width, float height, float depth, int divisions);

    /**
     * @deprecated Use ConeShapeBuilder.build instead.
     */
    @Deprecated
    void cone(float width, float height, float depth, int divisions, float angleFrom, float angleTo);

    /**
     * @deprecated Use SphereShapeBuilder.build instead.
     */
    @Deprecated
    void sphere(float width, float height, float depth, int divisionsU, int divisionsV);

    /**
     * @deprecated Use SphereShapeBuilder.build instead.
     */
    @Deprecated
    void sphere(final Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV);

    /**
     * @deprecated Use SphereShapeBuilder.build instead.
     */
    @Deprecated
    void sphere(float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom,
                float angleUTo,
                float angleVFrom, float angleVTo);

    /**
     * @deprecated Use SphereShapeBuilder.build instead.
     */
    @Deprecated
    void sphere(final Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV,
                float angleUFrom, float angleUTo, float angleVFrom, float angleVTo);

    /**
     * @deprecated Use CapsuleShapeBuilder.build instead.
     */
    @Deprecated
    void capsule(float radius, float height, int divisions);

    /**
     * @deprecated Use ArrowShapeBuilder.build instead.
     */
    @Deprecated
    void arrow(float x1, float y1, float z1, float x2, float y2, float z2, float capLength, float stemThickness,
               int divisions);
}

