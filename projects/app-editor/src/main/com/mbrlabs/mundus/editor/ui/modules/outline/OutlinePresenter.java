package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.assets.EditorTerrainService;
import com.mbrlabs.mundus.editor.core.ecs.EditorEcsService;
import com.mbrlabs.mundus.editor.core.light.LightService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
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
    private final EditorEcsService editorEcsService;

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

        initAddShaderButton(outline);
        initAddGroupButton(outline);
        initAddCameraButton(outline);
        initAddTerrainButton(outline.getRcmAddTerrain());
        initAddDirectionLightButton(outline);
        initDeleteButton(outline);
        initRenameButton(outline);
        initDuplicateButton(outline);
    }

    public void moveCameraToSelectedEntity(int entityId) {
        var pos = new Vector3();
        var positionComponent = ctx.getCurrentWorld().getEntity(entityId).getComponent(PositionComponent.class);
        if (positionComponent == null) {
            return;
        }
        positionComponent.getTransform().getTranslation(pos);
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

            log.trace("Delete entity with id {}", outline.getSelectedEntityId());
            int selectedParent = editorEcsService.removeEntity(ctx.getCurrentWorld(), outline.getSelectedEntityId());

            eventBus.post(new SceneGraphChangedEvent());
            eventBus.post(new EntitySelectedEvent(selectedParent));
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

    private void initAddCameraButton(Outline outline) {
        outline.getRcmAddCamera().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var createdId = cameraService.createEcsCamera(getParentEntity(outline));
                eventBus.post(new SceneGraphChangedEvent());
                eventBus.post(new EntitySelectedEvent(createdId));
            }
        });
    }

    private void initAddShaderButton(Outline outline) {
        outline.getRcmAddShader().addListener(new ClickButtonListener(() -> {
            var asset = assetsStorage.createShader();
            ctx.getCurrent().getCurrentScene().getAssets().add(asset);
            eventBus.post(new SceneGraphChangedEvent());
        }));
    }

    private int getParentEntity(Outline outline) {
        if (outline.getSelectedEntityId() < 0) {
            return -1;
        }
        var typeComponent = ctx.getCurrentWorld().getEntity(outline.getSelectedEntityId())
                .getComponent(TypeComponent.class);
        if (typeComponent != null && typeComponent.getType() == TypeComponent.Type.GROUP) {
            return outline.getSelectedEntityId();
        }

        return -1;
    }

    private void initAddDirectionLightButton(Outline outline) {
        outline.getAddDirectionalLight().addListener(new ClickButtonListener(() -> {
            var createdId = lightService.createDirectionLight(getParentEntity(outline));
            eventBus.post(new SceneGraphChangedEvent());
            eventBus.post(new EntitySelectedEvent(createdId));
        }));
    }

    private void initAddGroupButton(Outline outline) {
        outline.getRcmAddGroup().addListener(new ClickButtonListener(() -> {
            var id = ctx.getCurrentWorld().create();
            editorEcsService.addEntityBaseComponents(ctx.getCurrentWorld(), id, getParentEntity(outline),
                    "Group " + id, new TypeComponent(TypeComponent.Type.GROUP));

            eventBus.post(new SceneGraphChangedEvent());
            eventBus.post(new EntitySelectedEvent(id));
        }));
    }


    @Nullable
    public OutlineDragAndDrop.DropListener getDropListener(Outline outline) {
        return new OutlineDragAndDrop.DropListener() {
            @Override
            public void movedToRoot(int entityId) {
                throw new NotImplementedException();
//                ctx.getCurrent().getCurrentScene().getSceneGraph().addGameObject(obj);
            }

            @Override
            public void updateTree() {
                outline.buildTree(ctx.getCurrent().getCurrentScene());
            }
        };
    }
}
