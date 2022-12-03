package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.components.EditorCameraComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutlinePresenter {

    private final EditorCtx ctx;
    private final EventBus eventBus;
    private final AppUi appUi;
    private final ToolManager toolManager;
    private final AssetsStorage assetsStorage;

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

        initAddShaderButton(outline, outline.getRightClickMenu().getAddShader());
        initAddGroupButton(outline, outline.getRightClickMenu().getAddGroup());
        initAddCameraButton(outline, outline.getRightClickMenu().getAddCamera());
    }

    private void initAddCameraButton(Outline outline, MenuItem addCamera) {
        addCamera.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var id = ctx.getCurrent().obtainID();
                //todo check selected game object
                var go = new GameObject("Camera " + id, id);
                go.addComponent(new EditorCameraComponent(go, new PerspectiveCamera()));
                ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(go);

                //todo strange action, may be rebuild tree?
                outline.addGoToTree(null, go);
                eventBus.post(new SceneGraphChangedEvent());
            }
        });
    }

    private void initAddShaderButton(Outline outline, MenuItem menuItem) {
        menuItem.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var asset = assetsStorage.createShader();
                ctx.getCurrent().getCurrentScene().getAssets().add(asset);

                outline.buildTree(ctx.getCurrent().getCurrentScene());
            }
        });
    }

    private void initAddGroupButton(Outline outline, MenuItem menuItem) {
        menuItem.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // the new game object
                var go = new GameObject(GameObject.DEFAULT_NAME, ctx.getCurrent().obtainID());
                // update outline
                var selectedGO = outline.getRightClickMenu().getSelectedGO();
                if (selectedGO == null) {
                    // update sceneGraph
                    log.trace("Add empty game object [{}] in root node.", go);
                    ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(go);
                    // update outline
                    outline.addGoToTree(null, go);
                } else {
                    log.trace("Add empty game object [{}] child in node [{}].", go, selectedGO);
                    // update sceneGraph
                    selectedGO.addChild(go);
                    // update outline
                    var n = outline.getTree().findNode(selectedGO);
                    outline.addGoToTree(n, go);
                }
                eventBus.post(new SceneGraphChangedEvent());
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
