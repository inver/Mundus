package com.mbrlabs.mundus.editor.ui;

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;

public final class IconUtils {
    public static SymbolIcon getIcon(Asset<?> asset) {
        switch (asset.getType()) {
            case TERRAIN:
                return SymbolIcon.TERRAIN;
            case MATERIAL:
                return SymbolIcon.MATERIAL;
            case MODEL:
                return SymbolIcon.MODEL;
            case TEXTURE:
                return SymbolIcon.TEXTURE;
            case SKYBOX:
                return SymbolIcon.SKYBOX;
        }

        //todo add default icon
        return SymbolIcon.SCENE;
    }
}
