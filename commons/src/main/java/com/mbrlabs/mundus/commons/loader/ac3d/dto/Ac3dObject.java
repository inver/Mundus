package com.mbrlabs.mundus.commons.loader.ac3d.dto;

import com.mbrlabs.mundus.commons.dto.Matrix3Dto;
import com.mbrlabs.mundus.commons.dto.Vector2Dto;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;
import com.mbrlabs.mundus.commons.dto.vertex.VertexDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Ac3dObject {
    private static final float SCALE = 1.0f;

    private final Ac3dObjectType type;

    private final String name;
    private final List<String> data;

    private final String texturePath;
    private final Vector2Dto textureOffset;
    private final Vector2Dto textureRepeat;

    //Optional - default 0. Change subdivision level. Also at the same time the mesh will be smoothed.
    private final int subdivisionLevel;
    //The angle in degrees between normals, below which smoothing will take place for smooth surfaces.
    private final float crease;

    private final String url;

    private Matrix3Dto rotation;
    private Vector3Dto translation;

    private boolean hidden;
    private boolean locked;
    //if this object should be folded (collapsed) in the hierarchy dialog.
    private boolean folded;

    private final List<VertexDto> vertices;

    private final List<Ac3dSurface> surfaces;

    private final List<Ac3dObject> children;
}
