package com.mbrlabs.mundus.commons.loader.obj.builder;

import com.mbrlabs.mundus.commons.dto.Vector3Dto;
import com.mbrlabs.mundus.commons.dto.vertex.VertexDto;
import com.mbrlabs.mundus.commons.dto.vertex.VertexTextureDto;

public class FaceVertex {
    int index = -1;
    public VertexDto vertex = null;
    public VertexTextureDto texture = null;
    public Vector3Dto normal = null;

    public String toString() {
        return vertex + "|" + normal + "|" + texture;
    }
}