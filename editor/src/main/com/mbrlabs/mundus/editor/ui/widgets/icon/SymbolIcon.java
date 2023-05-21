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
    IMPORT("\ue9fc");

    private final String symbol;

    SymbolIcon(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
