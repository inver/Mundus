package com.mbrlabs.mundus.commons.loader.obj.builder;

import com.mbrlabs.mundus.commons.dto.Vector3Dto;

import java.util.ArrayList;
import java.util.List;

public class Face {

    public List<FaceVertex> vertices = new ArrayList<>();
    public ObjMaterial material = null;
    public ObjMaterial map = null;

    public Face() {
    }

    public void add(FaceVertex vertex) {
        vertices.add(vertex);
    }

    public Vector3Dto faceNormal = new Vector3Dto(0, 0, 0);

    // @TODO: This code assumes the face is a triangle.  
    public void calculateTriangleNormal() {
        float[] edge1 = new float[3];
        float[] edge2 = new float[3];
        float[] normal = new float[3];
        var v1 = vertices.get(0).vertex;
        var v2 = vertices.get(1).vertex;
        var v3 = vertices.get(2).vertex;
        float[] p1 = {v1.getX(), v1.getY(), v1.getZ()};
        float[] p2 = {v2.getX(), v2.getY(), v2.getZ()};
        float[] p3 = {v3.getX(), v3.getY(), v3.getZ()};

        edge1[0] = p2[0] - p1[0];
        edge1[1] = p2[1] - p1[1];
        edge1[2] = p2[2] - p1[2];

        edge2[0] = p3[0] - p2[0];
        edge2[1] = p3[1] - p2[1];
        edge2[2] = p3[2] - p2[2];

        normal[0] = edge1[1] * edge2[2] - edge1[2] * edge2[1];
        normal[1] = edge1[2] * edge2[0] - edge1[0] * edge2[2];
        normal[2] = edge1[0] * edge2[1] - edge1[1] * edge2[0];

        faceNormal = new Vector3Dto(normal);
    }

    public String toString() {
        StringBuilder result = new StringBuilder("\tvertices: " + vertices.size() + " :\n");
        for (FaceVertex f : vertices) {
            result.append(" \t\t( ").append(f.toString()).append(" )\n");
        }
        return result.toString();
    }
}  