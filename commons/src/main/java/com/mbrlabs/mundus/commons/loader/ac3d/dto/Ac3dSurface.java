package com.mbrlabs.mundus.commons.loader.ac3d.dto;

import com.mbrlabs.mundus.commons.dto.Vector2DTO;
import com.mbrlabs.mundus.commons.dto.VertexDTO;
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

    private final List<Pair<Integer, Vector2DTO>> verticesRefs;
    private final List<Pair<VertexDTO, Vector2DTO>> vertices = new ArrayList<>();

    public enum Type {
        POLYGON, CLOSED_LINE, LINE
    }
}
