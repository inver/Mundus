package com.mbrlabs.mundus.editor.events;

import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectChangedEvent implements Event {
    @Getter
    private final ProjectContext projectContext;

    public interface ProjectChangedListener {
        @Subscribe
        void onProjectChanged(ProjectChangedEvent event);
    }
}
