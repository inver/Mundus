package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.assets.EditorTerrainService;
import com.mbrlabs.mundus.editor.core.light.LightService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.components.camera.CameraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
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
    private final CameraService cameraService;
    private final EditorTerrainService terrainService;
    private final ProjectManager projectManager;
    private final LightService lightService;

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
                var entityId = clickedNode.getValue();
                if (entityId < 0) {
                    return;
                }

                var pos = new Vector3();
                ctx.getCurrentWorld().getEntity(entityId).getComponent(PositionComponent.class)
                        .getTransform().getTranslation(pos);

                var cam = ctx.getCurrent().getCamera();
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
                int id = -1;
                if (node != null) {
                    id = node.getValue();
                }
                outline.getRightClickMenu().show(id, Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
            }
        });

        outline.getTree().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var selection = outline.getTree().getSelection();
                if (selection == null || selection.size() == 0) {
                    return;
                }

                var entityId = selection.first().getValue();
                ctx.selectEntity(entityId);
                toolManager.translateTool.entitySelected(entityId);

                eventBus.post(new EntitySelectedEvent(entityId));
            }
        });

        initAddShaderButton(outline.getRightClickMenu().getAddShader());
        initAddGroupButton(outline, outline.getRightClickMenu().getAddGroup());
        initAddCameraButton(outline.getRightClickMenu().getAddCamera());
        initAddTerrainButton(outline.getRightClickMenu().getAddTerrain());
        initAddDirectionLightButton(outline.getRightClickMenu().getAddDirectionalLight());
    }

    private void initAddTerrainButton(MenuItem addTerrain) {
        addTerrain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    log.debug("Add terrain game object in root node.");

                    var node = terrainService.createTerrainEntity();
                    ctx.getCurrent().getCurrentScene().getRootNode().addChild(node);

                    projectManager.saveProject(ctx.getCurrent());
                    //todo is needed import event here?
//                    eventBus.post(new AssetImportEvent(asset));
                    eventBus.post(new SceneGraphChangedEvent());
                } catch (Exception e) {
                    log.error("ERROR", e);
                }
            }
        });
    }

//    fun createTerrainGO(
//            shader:BaseShader,
//            goID:Int,
//            goName:String,
//            terrain:TerrainAsset,
//            pickShader:BaseShader
//    ):
//
//    GameObject {
//        val terrainGO = GameObject(null as String, goID)
//        terrainGO.name = goName
//
//        terrain.terrain.setTransform(terrainGO.transform)
//        val terrainComponent = PickableTerrainComponent(terrainGO, pickShader)
//        terrainComponent.terrain = terrain
//        terrainGO.components.add(terrainComponent)
//        terrainComponent.shader = shader
//        terrainComponent.encodeRayPickColorId()
//
//        return terrainGO
//    }

    private void initAddCameraButton(MenuItem addCamera) {
        addCamera.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var cameraNode = cameraService.createEcsCamera();
                ctx.getCurrent().getCurrentScene().getRootNode().addChild(cameraNode);
                eventBus.post(new SceneGraphChangedEvent());
            }
        });
    }

    private void initAddShaderButton(MenuItem menuItem) {
        menuItem.addListener(new ClickButtonListener(() -> {
            var asset = assetsStorage.createShader();
            ctx.getCurrent().getCurrentScene().getAssets().add(asset);
            eventBus.post(new SceneGraphChangedEvent());
        }));
    }

    private void initAddDirectionLightButton(MenuItem menuItem) {
        menuItem.addListener(new ClickButtonListener(() -> {
            var node = lightService.createDirectionLight();
            ctx.getCurrent().getCurrentScene().getRootNode().addChild(node);
            eventBus.post(new SceneGraphChangedEvent());
        }));
    }

    private void initAddGroupButton(Outline outline, MenuItem menuItem) {
        menuItem.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var node = new HierarchyNode(-1, "Group");
                // the new game object
                var go = new GameObject(GameObject.DEFAULT_NAME, ctx.getCurrent().obtainID());
                // update outline
                var selectedGO = outline.getRightClickMenu().getSelectedGO();
                if (selectedGO == -1) {
                    // update sceneGraph
                    log.trace("Add empty game object [{}] in root node.", go);
//                    ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(go);
                    // update outline
                    ctx.getCurrent().getCurrentScene().getRootNode().addChild(node);
                } else {
                    throw new NotImplementedException();
//                    log.trace("Add empty game object [{}] child in node [{}].", go, selectedGO);
//                    // update sceneGraph
//                    selectedGO.addChild(go);
//                    // update outline
//                    var n = outline.getTree().findNode(selectedGO);
//                    outline.addGoToTree(n, go);
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
//                ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(obj);
            }

            @Override
            public void updateTree() {
                outline.buildTree(ctx.getCurrent().getCurrentScene());
            }
        };
    }
}
