package com.mbrlabs.mundus.commons.loader.ac3d.dto;

import com.mbrlabs.mundus.commons.dto.Vector2Dto;
import com.mbrlabs.mundus.commons.dto.vertex.VertexDto;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class Ac3dSurface {

    private final Type type;
    private final boolean shaded;
    private final boolean twoSided;
    private final int materialIndex;

    private final List<Pair<Integer, Vector2Dto>> verticesRefs;
    private final List<Pair<VertexDto, Vector2Dto>> vertices = new ArrayList<>();

    public enum Type {
        POLYGON, CLOSED_LINE, LINE
    }
}
