package com.mbrlabs.mundus.commons.assets.model;

import com.badlogic.gdx.utils.ObjectMap;
import lombok.Getter;

@Getter
public class ModelMeta {
    private final ObjectMap<String, String> materials = new ObjectMap<>();
}
