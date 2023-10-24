package com.mbrlabs.mundus.commons.assets.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ModelMeta {
    private String file;
    private Format format;
    private boolean binary;

    public enum Format {
        GLTF
    }

    private final List<String> materials = new ArrayList<>();
}
