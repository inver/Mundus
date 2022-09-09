package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.widget.VisTree;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.AppUi;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutlinePresenter {
    private final EventBus eventBus;
    private final ProjectManager projectManager;
    private final AppUi appUi;

    public void init(@NotNull Outline outline) {
        eventBus.register(outline);
        outline.getScrollPane().addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                appUi.setScrollFocus(outline.getScrollPane());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                appUi.setScrollFocus(null);
            }
        });
    }

    @Nullable
    public OutlineDragAndDrop.DropListener getDropListener(Outline outline) {
        return new OutlineDragAndDrop.DropListener() {
            @Override
            public void movedToRoot(GameObject obj) {
                projectManager.getCurrent().currScene.sceneGraph.addGameObject(obj);
            }

            @Override
            public void updateTree() {
                outline.buildTree(projectManager.getCurrent().currScene.sceneGraph);
            }
        };
    }
}
