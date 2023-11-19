package com.mbrlabs.mundus.editor.core.ui;

import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentWidget;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UiBundle {

    private UiBundleMeta meta;

    private UiComponentWidget widget;
    
}
