package com.mbrlabs.mundus.commons.assets.texture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextureMeta {
    private String file;
    private boolean tileable;
    private boolean generateMipMaps;
}
