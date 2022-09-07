package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.mbrlabs.mundus.editor.events.EventBus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutlinePresenter {
    private final EventBus eventBus;

    public void init(@NotNull Outline outline) {
        eventBus.register(outline);
    }
}
