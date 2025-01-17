/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableSceneObject;
import com.mbrlabs.mundus.commons.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.nevinsky.abyssus.core.ModelBuilder;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.mesh.Mesh;
import net.nevinsky.abyssus.core.mesh.MeshBuilder;
import net.nevinsky.abyssus.core.mesh.MeshPart;
import net.nevinsky.abyssus.core.mesh.MeshPartBuilder;
import net.nevinsky.abyssus.core.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 30-11-2015
 */
public class TerrainObject implements RenderableSceneObject, Disposable {

    public static final int DEFAULT_SIZE = 1600;
    public static final int DEFAULT_VERTEX_RESOLUTION = 180;
    public static final int DEFAULT_UV_SCALE = 60;

    private static final MeshPartBuilder.VertexInfo tempVertexInfo = new MeshPartBuilder.VertexInfo();
    private static final Vector3 c00 = new Vector3();
    private static final Vector3 c01 = new Vector3();
    private static final Vector3 c10 = new Vector3();
    private static final Vector3 c11 = new Vector3();

    public Matrix4 transform;
    public float[] heightData;
    public int terrainWidth = 1200;
    public int terrainDepth = 1200;
    public int vertexResolution;

    // used for building the mesh
    private final VertexAttributes attributes;
    private Vector2 uvScale = new Vector2(DEFAULT_UV_SCALE, DEFAULT_UV_SCALE);
    private float[] vertices;
    private final int stride;
    private final int posPos;
    private final int norPos;
    private final int uvPos;

    @Getter
    private final String assetName;

    // Textures
    private TerrainTexture terrainTexture;
    private final transient  Material material;
    // Mesh
    private transient Model model;
    @Getter
    public transient ModelInstance modelInstance;
    private transient Mesh mesh;

    private TerrainObject(TerrainAsset asset, int vertexResolution) {
        this.assetName = asset.getName();
        this.transform = new Matrix4();
        this.attributes = MeshBuilder.createAttributes(
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates
        );
        this.posPos = attributes.getOffset(VertexAttributes.Usage.Position, -1);
        this.norPos = attributes.getOffset(VertexAttributes.Usage.Normal, -1);
        this.uvPos = attributes.getOffset(VertexAttributes.Usage.TextureCoordinates, -1);
        this.stride = attributes.vertexSize / 4;

        this.vertexResolution = vertexResolution;
        this.heightData = new float[vertexResolution * vertexResolution];

        this.terrainTexture = new TerrainTexture();
        material = new Material();
        material.set(new TerrainTextureAttribute(TerrainTextureAttribute.ATTRIBUTE_SPLAT0, terrainTexture));
    }

    public TerrainObject(TerrainAsset asset, int size, float[] heightData) {
        this(asset, (int) Math.sqrt(heightData.length));
        this.terrainWidth = size;
        this.terrainDepth = size;
        this.heightData = heightData;
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
        modelInstance.transform = this.transform;
    }

    public void init() {
        final int numVertices = vertexResolution * vertexResolution;
        final int numIndices = (vertexResolution - 1) * (vertexResolution - 1) * 6;

        mesh = new Mesh(true, numVertices, numIndices, attributes);
        this.vertices = new float[numVertices * stride];
        mesh.setIndices(buildIndices());
        buildVertices();
        mesh.setVertices(vertices);

        var meshPart = new MeshPart(null, mesh, 0, numIndices, GL20.GL_TRIANGLES);
        meshPart.update();

        var mb = new ModelBuilder();
        mb.begin();
        mb.part(meshPart, material);
        model = mb.end();
        modelInstance = new ModelInstance(model);
        modelInstance.transform = transform;
    }

    public Vector3 getVertexPosition(Vector3 out, int x, int z) {
        final float dx = (float) x / (float) (vertexResolution - 1);
        final float dz = (float) z / (float) (vertexResolution - 1);
        final float height = heightData[z * vertexResolution + x];
        out.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        return out;
    }

    public float getHeightAtWorldCoord(float worldX, float worldZ) {
        transform.getTranslation(c00);
        float terrainX = worldX - c00.x;
        float terrainZ = worldZ - c00.z;

        float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        c01.set(1, heightData[(gridZ + 1) * vertexResolution + gridX], 0);
        c10.set(0, heightData[gridZ * vertexResolution + gridX + 1], 1);

        // we are in upper left triangle of the square
        if (xCoord <= (1 - zCoord)) {
            c00.set(0, heightData[gridZ * vertexResolution + gridX], 0);
            return MathUtils.barryCentric(c00, c10, c01, new Vector2(zCoord, xCoord));
        }
        // bottom right triangle
        c11.set(1, heightData[(gridZ + 1) * vertexResolution + gridX + 1], 1);
        return MathUtils.barryCentric(c10, c11, c01, new Vector2(zCoord, xCoord));
    }

    public Vector3 getRayIntersection(Vector3 out, Ray ray) {
        // TODO improve performance. use binary search
        float curDistance = 2;
        int rounds = 0;

        ray.getEndPoint(out, curDistance);
        boolean isUnder = isUnderTerrain(out);

        while (true) {
            rounds++;
            ray.getEndPoint(out, curDistance);

            boolean u = isUnderTerrain(out);
            if (u != isUnder || rounds == 10000) {
                return out;
            }
            curDistance += u ? -0.1f : 0.1f;
        }

    }

    private int[] buildIndices() {
        final int w = vertexResolution - 1;
        final int h = vertexResolution - 1;
        int[] indices = new int[w * h * 6];
        int i = -1;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final int c00 = y * vertexResolution + x;
                final int c10 = c00 + 1;
                final int c01 = c00 + vertexResolution;
                final int c11 = c10 + vertexResolution;
                indices[++i] = c11;
                indices[++i] = c10;
                indices[++i] = c00;
                indices[++i] = c00;
                indices[++i] = c01;
                indices[++i] = c11;
            }
        }
        return indices;
    }

    private void buildVertices() {
        for (int x = 0; x < vertexResolution; x++) {
            for (int z = 0; z < vertexResolution; z++) {
                calculateVertexAt(tempVertexInfo, x, z);
                calculateNormalAt(tempVertexInfo, x, z);
                setVertex(z * vertexResolution + x, tempVertexInfo);
            }
        }
    }

    private void setVertex(int index, MeshPartBuilder.VertexInfo info) {
        index *= stride;
        if (posPos >= 0) {
            vertices[index + posPos] = info.position.x;
            vertices[index + posPos + 1] = info.position.y;
            vertices[index + posPos + 2] = info.position.z;
        }
        if (uvPos >= 0) {
            vertices[index + uvPos] = info.uv.x;
            vertices[index + uvPos + 1] = info.uv.y;
        }
        if (norPos >= 0) {
            vertices[index + norPos] = info.normal.x;
            vertices[index + norPos + 1] = info.normal.y;
            vertices[index + norPos + 2] = info.normal.z;
        }
    }

    private MeshPartBuilder.VertexInfo calculateVertexAt(MeshPartBuilder.VertexInfo out, int x, int z) {
        final float dx = (float) x / (float) (vertexResolution - 1);
        final float dz = (float) z / (float) (vertexResolution - 1);
        final float height = heightData[z * vertexResolution + x];

        out.position.set(dx * this.terrainWidth, height, dz * this.terrainDepth);
        out.uv.set(dx, dz).scl(uvScale);

        return out;
    }

    public void updateUvScale(Vector2 uvScale) {
        this.uvScale = uvScale;
    }

    public Vector2 getUvScale() {
        return uvScale;
    }

    /**
     * Calculates normal of a vertex at x,y based on the verticesOnZ of the surrounding vertices
     */
    private MeshPartBuilder.VertexInfo calculateNormalAt(MeshPartBuilder.VertexInfo out, int x, int y) {
        out.normal.set(getNormalAt(x, y));
        return out;
    }

    /**
     * Get normal at world coordinates. The methods calculates exact point position in terrain coordinates and returns
     * normal at that point. If point doesn't belong to terrain -- it returns default
     * <code>Vector.Y<code> normal.
     *
     * @param worldX the x coord in world
     * @param worldZ the z coord in world
     * @return normal at that point. If point doesn't belong to terrain -- it returns default <code>Vector.Y<code>
     * normal.
     */
    public Vector3 getNormalAtWordCoordinate(float worldX, float worldZ) {
        transform.getTranslation(c00);
        float terrainX = worldX - c00.x;
        float terrainZ = worldZ - c00.z;

        float gridSquareSize = terrainWidth / ((float) vertexResolution - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if (gridX >= vertexResolution - 1 || gridZ >= vertexResolution - 1 || gridX < 0 || gridZ < 0) {
            return Vector3.Y.cpy();
        }

        return getNormalAt(gridX, gridZ);
    }

    /**
     * Get Normal at x,y point of terrain
     *
     * @param x the x coord on terrain
     * @param y the y coord on terrain( actual z)
     * @return the normal at the point of terrain
     */
    public Vector3 getNormalAt(int x, int y) {
        Vector3 out = new Vector3();
        // handle edges of terrain
        int xP1 = (x + 1 >= vertexResolution) ? vertexResolution - 1 : x + 1;
        int yP1 = (y + 1 >= vertexResolution) ? vertexResolution - 1 : y + 1;
        int xM1 = Math.max(x - 1, 0);
        int yM1 = Math.max(y - 1, 0);

        float hL = heightData[y * vertexResolution + xM1];
        float hR = heightData[y * vertexResolution + xP1];
        float hD = heightData[yM1 * vertexResolution + x];
        float hU = heightData[yP1 * vertexResolution + x];
        out.x = hL - hR;
        out.y = 2;
        out.z = hD - hU;
        out.nor();
        return out;
    }

    public boolean isUnderTerrain(Vector3 worldCoords) {
        float terrainHeight = getHeightAtWorldCoord(worldCoords.x, worldCoords.z);
        return terrainHeight > worldCoords.y;
    }

    public boolean isOnTerrain(float worldX, float worldZ) {
        transform.getTranslation(c00);
        return worldX >= c00.x && worldX <= c00.x + terrainWidth && worldZ >= c00.z && worldZ <= c00.z + terrainDepth;
    }

    public Vector3 getPosition(Vector3 out) {
        transform.getTranslation(out);
        return out;
    }

    public TerrainTexture getTerrainTexture() {
        return terrainTexture;
    }

    public void setTerrainTexture(TerrainTexture terrainTexture) {
        if (terrainTexture == null) {
            return;
        }

        this.terrainTexture = terrainTexture;

        material.set(new TerrainTextureAttribute(TerrainTextureAttribute.ATTRIBUTE_SPLAT0, this.terrainTexture));
    }

    public void update() {
        buildVertices();
        mesh.setVertices(vertices);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        modelInstance.userData = new TerrainUserData(terrainWidth, terrainDepth);
        modelInstance.getRenderables(renderables, pool);
    }

    @Override
    public void dispose() {

    }

    @Override
    public AssetType getType() {
        return AssetType.TERRAIN;
    }

    @Data
    @AllArgsConstructor
    public static class TerrainUserData {
        private int terrainWidth;
        private int terrainDepth;

        private final List<BaseLight<?>> lights = new ArrayList<>();
    }
}
