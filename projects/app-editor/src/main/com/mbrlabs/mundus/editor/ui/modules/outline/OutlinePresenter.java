package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.assets.EditorTerrainService;
import com.mbrlabs.mundus.editor.core.ecs.DependenciesComponent;
import com.mbrlabs.mundus.editor.core.light.LightService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.core.scene.SceneService;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.tools.ToolManager;
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
    private final ToolManager toolManager;
    private final AssetsStorage assetsStorage;
    private final CameraService cameraService;
    private final EditorTerrainService terrainService;
    private final ProjectManager projectManager;
    private final LightService lightService;
    private final SceneService sceneService;

    public void init(@NotNull Outline outline) {
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            log.trace("Project changed. Building scene graph.");
            outline.buildTree(ctx.getCurrent().getCurrentScene());
        });
        eventBus.register((SceneChangedEvent.SceneChangedListener) event -> {
            log.trace("Scene changed. Building scene graph.");
            outline.buildTree(ctx.getCurrent().getCurrentScene());
        });
        eventBus.register((SceneGraphChangedEvent.SceneGraphChangedListener) event -> {
            log.trace("SceneGraph changed. Building scene graph.");
            outline.buildTree(ctx.getCurrent().getCurrentScene());
        });
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            outline.onEntitySelected(event.getEntityId());
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

        initAddShaderButton(outline.getRcmAddShader());
        initAddGroupButton(outline);
        initAddCameraButton(outline.getRcmAddCamera());
        initAddTerrainButton(outline.getRcmAddTerrain());
        initAddDirectionLightButton(outline.getAddDirectionalLight());
        initDeleteButton(outline);
        initRenameButton(outline);
        initDuplicateButton(outline);
    }

    public void moveCameraToSelectedEntity(int entityId) {
        var pos = new Vector3();
        ctx.getCurrentWorld().getEntity(entityId)
                .getComponent(PositionComponent.class)
                .getTransform().getTranslation(pos);
        var cam = ctx.getCurrent().getCamera();
        // just lerp in the direction of the object if certain distance away
        if (pos.dst(cam.position) > 100) {
            cam.position.lerp(pos.cpy().add(0f, 40f, 0f), 0.5f);
        }
        cam.lookAt(pos);
        cam.up.set(Vector3.Y);
    }

    private void initAddTerrainButton(MenuItem addTerrain) {
        addTerrain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    log.debug("Add terrain game object in root node.");

                    var node = terrainService.createTerrain();
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

    private void initRenameButton(Outline outline) {
        outline.getRcmRename().addListener(new ClickButtonListener(() -> {
            if (outline.getSelectedEntityId() > 0) {
                outline.showRenameDialog();
            }
        }));
    }

    private void initDuplicateButton(Outline outline) {
        outline.getRcmDuplicate().addListener(new ClickButtonListener(() -> {
            if (outline.getSelectedEntityId() > 0 && !outline.getRcmDuplicate().isDisabled()) {
                throw new NotImplementedException();
                //                        duplicateGO(selectedGO!!, selectedGO!!.parent)
//                        eventBus.post(SceneGraphChangedEvent())            }
            }
        }));
    }

    private void initDeleteButton(Outline outline) {
        outline.getRcmDelete().addListener(new ClickButtonListener(() -> {
            if (outline.getSelectedEntityId() < 0) {
                return;
            }

            var node = sceneService.find(
                    ctx.getCurrent().getCurrentScene().getRootNode(), outline.getSelectedEntityId()
            );
            if (node == null) {
                return;
            }

            if (node.getType() == HierarchyNode.Type.GROUP) {
                ctx.getCurrentWorld().delete(outline.getSelectedEntityId());
            } else {
                ctx.getCurrentWorld().getMapper(DependenciesComponent.class)
                        .get(outline.getSelectedEntityId())
                        .getDependencies()
                        .forEach(d -> ctx.getCurrentWorld().delete(d));
            }
            //TODO remove search of node in #sceneService.deleteNode
            sceneService.deleteNode(
                    ctx.getCurrent().getCurrentScene().getRootNode(), outline.getSelectedEntityId(),
                    node.getType() == HierarchyNode.Type.GROUP
            );


            eventBus.post(new SceneGraphChangedEvent());
            //todo
//            val deleteCommand = DeleteCommand(selectedEntityId, tree.findNode(selectedEntityId))
//            history.add(deleteCommand)
        }));
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

    private void initAddGroupButton(Outline outline) {
        outline.getRcmAddGroup().addListener(new ClickButtonListener(() -> {
            var node = new HierarchyNode(-1, "Group");
            throw new NotImplementedException();
            // the new game object
//                var go = new GameObject(GameObject.DEFAULT_NAME, ctx.getCurrent().obtainID());
            // update outline
//                var selectedGO = outline.getRightClickMenu().getSelectedEntityId();
//                if (selectedGO == -1) {
//                    // update sceneGraph
////                    log.trace("Add empty game object [{}] in root node.", go);
////                    ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(go);
//                    // update outline
//                    ctx.getCurrent().getCurrentScene().getRootNode().addChild(node);
//                } else {
//                    throw new NotImplementedException();
////                    log.trace("Add empty game object [{}] child in node [{}].", go, selectedGO);
////                    // update sceneGraph
////                    selectedGO.addChild(go);
////                    // update outline
////                    var n = outline.getTree().findNode(selectedGO);
////                    outline.addGoToTree(n, go);
//                }
//                eventBus.post(new SceneGraphChangedEvent());
        }));
    }


    @Nullable
    public OutlineDragAndDrop.DropListener getDropListener(Outline outline) {
        return new OutlineDragAndDrop.DropListener() {
            @Override
            public void movedToRoot(int entityId) {
//                ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(obj);
            }

            @Override
            public void updateTree() {
                outline.buildTree(ctx.getCurrent().getCurrentScene());
            }
        };
    }
}
