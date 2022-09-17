package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.AppUi;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutlinePresenter {

    private final EditorCtx ctx;
    private final EventBus eventBus;
    private final ProjectManager projectManager;
    private final AppUi appUi;
    private final ToolManager toolManager;

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
        outline.getTree().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() != 2) {
                    return;
                }
                var clickedNode = outline.getTree().getNodeAt(y);
                if (clickedNode == null) {
                    return;
                }
                var go = clickedNode.getValue();
                if (go == null) {
                    return;
                }

                var pos = new Vector3();
                go.getTransform().getTranslation(pos);

                var cam = ctx.getCamera();
                // just lerp in the direction of the object if certain distance away
                if (pos.dst(cam.position) > 100) {
                    cam.position.lerp(pos.cpy().add(0f, 40f, 0f), 0.5f);
                }

                cam.lookAt(pos);
                cam.up.set(Vector3.Y);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Input.Buttons.LEFT != button) {
                    return true;
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Input.Buttons.RIGHT != button) {
                    super.touchUp(event, x, y, pointer, button);
                    return;
                }

                var node = outline.getTree().getNodeAt(y);
                GameObject go = null;
                if (node != null) {
                    go = node.getValue();
                }
                outline.getRightClickMenu().show(go, Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
            }
        });

        outline.getTree().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var selection = outline.getTree().getSelection();
                if (selection != null && selection.size() > 0) {
                    var go = selection.first().getValue();
                    ctx.setSelected(go);
                    toolManager.translateTool.gameObjectSelected(go);

                    eventBus.post(new GameObjectSelectedEvent(go));
                }
            }
        });
    }

    @Nullable
    public OutlineDragAndDrop.DropListener getDropListener(Outline outline) {
        return new OutlineDragAndDrop.DropListener() {
            @Override
            public void movedToRoot(GameObject obj) {
                ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(obj);
            }

            @Override
            public void updateTree() {
                outline.buildTree(ctx.getCurrent().getCurrentScene());
            }
        };
    }
}
