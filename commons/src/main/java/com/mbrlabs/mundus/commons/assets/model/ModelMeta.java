package com.mbrlabs.mundus.commons.assets.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModelMeta {
    private String file;
    private Format format;
    private boolean binary;

    public enum Format {
        GLTF;
    }
}
