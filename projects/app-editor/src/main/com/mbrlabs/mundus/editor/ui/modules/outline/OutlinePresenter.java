package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;
import com.kotcrab.vis.ui.util.dialog.InputDialogListener;
import com.kotcrab.vis.ui.widget.VisTree;
import com.mbrlabs.mundus.commons.core.ecs.component.NameComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.assets.EditorTerrainService;
import com.mbrlabs.mundus.editor.core.ecs.EcsService;
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
    private final EcsService ecsService;

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
                if (selection == null || selection.isEmpty()) {
                    return;
                }

                var entityId = selection.first().getValue();
                ctx.selectEntity(entityId);
                toolManager.translateTool.entitySelected(entityId);

                eventBus.post(new EntitySelectedEvent(entityId));
            }
        });
    }

    OutlineDragAndDropController initDragAndDropController(@NotNull VisTree<IdNode, Integer> tree) {
        return new OutlineDragAndDropController(ctx, eventBus, tree);
    }

    void moveCameraToSelectedEntity(int entityId) {
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

    Runnable addTerrainAction() {
        return () -> {
            try {
                log.debug("Add terrain game object in root node.");

                terrainService.createTerrain();

                projectManager.saveProject(ctx.getCurrent());
                //todo is needed import event here?
//                    eventBus.post(new AssetImportEvent(asset));
                eventBus.post(new SceneGraphChangedEvent());
            } catch (Exception e) {
                log.error("ERROR", e);
            }
        };
    }

    InputDialogListener renameListener(@NotNull Outline outline) {
        return new InputDialogAdapter() {
            @Override
            public void finished(String input) {
                log.trace("Rename game object [{}] to [{}].", outline.getSelectedEntityId(), input);

                var nameComponent = ctx.getCurrentWorld().getMapper(NameComponent.class)
                        .get(outline.getSelectedEntityId());
                nameComponent.setName(input);
                eventBus.post(new SceneGraphChangedEvent());
                eventBus.post(new EntitySelectedEvent(outline.getSelectedEntityId()));
            }
        };
    }

    Runnable duplicateAction(@NotNull Outline outline) {
        return () -> {
            if (outline.getSelectedEntityId() > 0 && !outline.getRcmDuplicate().isDisabled()) {
                throw new NotImplementedException();
                //                        duplicateGO(selectedGO!!, selectedGO!!.parent)
//                        eventBus.post(SceneGraphChangedEvent())            }
            }
        };
    }

    Runnable deleteAction(@NotNull Outline outline) {
        return () -> {
            if (outline.getSelectedEntityId() < 0) {
                return;
            }

            log.trace("Delete entity with id {}", outline.getSelectedEntityId());
            int selectedParent = ecsService.removeEntity(ctx.getCurrentWorld(), outline.getSelectedEntityId());

            eventBus.post(new SceneGraphChangedEvent());
            eventBus.post(new EntitySelectedEvent(selectedParent));
        };
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

    Runnable addCameraAction(@NotNull Outline outline) {
        return () -> {
            var createdId = cameraService.createEcsCamera(getParentEntity(outline));
            eventBus.post(new SceneGraphChangedEvent());
            eventBus.post(new EntitySelectedEvent(createdId));
        };
    }

    Runnable addShaderAction(@NotNull Outline outline) {
        return () -> {
            var asset = assetsStorage.createShader();
            ctx.getCurrent().getCurrentScene().getAssets().add(asset);
            eventBus.post(new SceneGraphChangedEvent());
        };
    }

    Runnable addDirectionLightAction(@NotNull Outline outline) {
        return () -> {
            var createdId = lightService.createDirectionLight(getParentEntity(outline));
            eventBus.post(new SceneGraphChangedEvent());
            eventBus.post(new EntitySelectedEvent(createdId));
        };
    }

    Runnable addGroupAction(@NotNull Outline outline) {
        return () -> {
            var id = ctx.getCurrentWorld().create();
            ecsService.addEntityBaseComponents(ctx.getCurrentWorld(), id, getParentEntity(outline),
                    "Group " + id, new TypeComponent(TypeComponent.Type.GROUP));

            eventBus.post(new SceneGraphChangedEvent());
            eventBus.post(new EntitySelectedEvent(id));
        };
    }

    private int getParentEntity(@NotNull Outline outline) {
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
}
