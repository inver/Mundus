package com.mbrlabs.mundus.editor.core.project;

import com.mbrlabs.mundus.commons.assets.Asset;

import java.util.HashMap;
import java.util.Map;

public class AssetsCtx {
    private final Map<String, Asset> assets = new HashMap<>();

    public void addAll(Map<String, Asset> assets) {
        this.assets.putAll(assets);
    }
}
