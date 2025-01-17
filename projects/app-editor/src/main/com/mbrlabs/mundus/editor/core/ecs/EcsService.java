package com.mbrlabs.mundus.editor.core.ecs;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.EntityEdit;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.core.ecs.component.NameComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.ParentComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.Point2PointPositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.editor.core.shader.ShaderConstants;
import com.mbrlabs.mundus.editor.ui.components.camera.CameraBodyRenderDelegate;
import com.mbrlabs.mundus.editor.ui.components.handle.DirectionHandleRenderDelegate;
import com.mbrlabs.mundus.editor.ui.components.handle.DirectionLineRenderDelegate;

@org.springframework.stereotype.Component
public class EcsService {
    private static final String HANDLE_NAME = "'Direction' handle";

    public EntityEdit addEntityBaseComponents(World world, int id, int parentEntityId, String name,
                                              Component... components) {
        var edit = world.edit(id)
                .add(new ParentComponent(parentEntityId))
                .add(new NameComponent(name));
        for (var c : components) {
            edit.add(c);
        }
        return edit;
    }

    public int createEntityWithDirection(World world, int parentId, Vector3 rootPosition, Vector3 handlePosition,
                                         String nodeName, RenderableDelegate renderableDelegate,
                                         RenderableDelegate pickerRenderableDelegate, Component... rootComponents) {
        int handleId = world.create();
        int rootId = world.create();
        int lineId = world.create();

        //todo move creation to archetypes

        // add handle component
        addEntityBaseComponents(world, handleId, rootId, HANDLE_NAME)
                .add(new TypeComponent(TypeComponent.Type.HANDLE))
                .add(new PositionComponent(handlePosition))
                .add(new DirectionHandleRenderDelegate().asComponent())
                .add(PickableComponent.of(handleId, new DirectionHandleRenderDelegate(ShaderConstants.PICKER)));

        // add root component, e.g. body of camera
        addEntityBaseComponents(world, rootId, parentId, nodeName + " " + rootId, rootComponents)
                .add(new PositionComponent(rootPosition, handleId))
                .add(renderableDelegate.asComponent())
                .add(PickableComponent.of(rootId, pickerRenderableDelegate))
                .add(new DependenciesComponent(lineId, rootId));

        world.edit(lineId)
                .add(new Point2PointPositionComponent(rootId, handleId))
                .add(new DirectionLineRenderDelegate().asComponent());

        return rootId;
    }

    public int removeEntity(World world, int selectedEntityId) {
        var entityIds = world.getAspectSubscriptionManager().get(Aspect.all(ParentComponent.class))
                .getEntities();
        var mapper = world.getMapper(ParentComponent.class);

        var selectedParent = mapper.get(selectedEntityId).getParentEntityId();
        for (int i = 0; i < entityIds.size(); i++) {
            var entityId = entityIds.get(i);
            var parentComponent = mapper.get(entityId);
            if (parentComponent.getParentEntityId() == selectedEntityId) {
                parentComponent.setParentEntityId(selectedParent);
            }
        }
        world.delete(selectedEntityId);
        return selectedParent;
    }
}
