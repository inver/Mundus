package com.mbrlabs.mundus.editor.ui.modules;

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.editor.ui.widgets.Icons;

public final class IconUtils {
    public static Icons getIcon(Asset asset) {
        switch (asset.getType()) {
            case TERRAIN:
                return Icons.TERRAIN;
            case MATERIAL:
                return Icons.MATERIAL;
            case MODEL:
                return Icons.MODEL;
            case TEXTURE:
                return Icons.TEXTURE;
        }

        //todo add default icon
        return Icons.SCENE;
    }
}
