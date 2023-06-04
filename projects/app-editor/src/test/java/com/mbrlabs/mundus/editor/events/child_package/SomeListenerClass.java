package com.mbrlabs.mundus.editor.events.child_package;

import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.LogEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SomeListenerClass {

    @Getter
    private int i = 0;
    private final EventBus eventBus;

    public void init() {
        eventBus.register((LogEvent.LogEventListener) event -> {
            i++;
        });
    }
}
