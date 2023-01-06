package com.mbrlabs.mundus.commons.assets.terrain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerrainMeta {
    private String terrainFile;
    private int size;
    private float uv;
    private String splatMap;
    private String splatBase;
    private String splatR;
    private String splatG;
    private String splatB;
    private String splatA;
}
