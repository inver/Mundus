package com.mbrlabs.mundus.editor.core.ui;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UiBundleMeta {
    private String inspectorWidget;
    private String outlineRightClickMenu;

    @Getter
    @Setter
    public static class OutlineRightClickMenu {
        private String id;
        private String text;
    }
}
