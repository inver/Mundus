package com.mbrlabs.mundus.editor.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
public class ProjectFileChangedEvent implements Event {
    @Getter
    private final Path path;

    public interface ProjectFileChangedListener {
        @Subscribe
        void onProjectFileChanged(ProjectFileChangedEvent event);
    }
}
