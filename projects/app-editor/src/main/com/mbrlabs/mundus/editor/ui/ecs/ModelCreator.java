package com.mbrlabs.mundus.editor.ui.ecs;

import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;

public interface ModelCreator {
    RenderableDelegate createRenderableDelegate(String shaderKey);

    String getClazz();
}
