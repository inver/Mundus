package com.mbrlabs.mundus.editor.ui.widgets.icon;

public enum SymbolIcon {
    MENU("\ue5d2"),
    SAVE("\ue161"),
    EXPORT("\ue6b8"),
    EXPAND_MORE("\ue5cf"),
    EXPAND_LESS("\ue5ce"),
    CLOSE("\ue5cd"),
    REFRESH("\ue863"),
    POINTER("\uf82f"),
    TRANSLATE("\ue89f"),
    EXPAND("\uf1ce"),
    CIRCLE("\uef4a"),
    STAR("\ue838"),
    SMOOTH_CIRCLE("\ue57b"),
    TAG("\ue9ef"),
    COFFEE("\uefef"),
    LANDSCAPE("\ue3f7"),
    LAYERS("\ue53b"),
    TEXTURE("\ue421"),
    CAMERA("\ue04b"),
    LIGHT("\ue42e"),
    DEBLUR("\ueb77"),
    CHEVRON_RIGHT("\ue5cc"),
    IMPORT("\ue9fc"),
    SKYBOX("\uea46"),
    SCENE("\ue2a7"),
    MATERIAL("\ue53b"),
    TERRAIN("\ue3f7"),
    MODEL("\uea1b"),
    SHADER("\ue3a3"),
    HELP("\ue887"),
    HIERARCHY("\uf535"),
    ASSET("\ue338");

    private final String symbol;

    SymbolIcon(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
