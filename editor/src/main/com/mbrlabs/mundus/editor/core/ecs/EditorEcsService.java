package com.mbrlabs.mundus.editor.core.ecs;

import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.core.ecs.EcsService;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.Point2PointPositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.model.ModelService;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import com.mbrlabs.mundus.editor.ui.components.handle.DirectionHandleRenderDelegate;
import com.mbrlabs.mundus.editor.ui.components.handle.DirectionLineRenderDelegate;

@org.springframework.stereotype.Component
public class EditorEcsService extends EcsService {

    private static final String HANDLE_NAME = "'Direction' handle";

    public EditorEcsService(AssetManager assetManager, TerrainService terrainService, ModelService modelService) {
        super(assetManager, terrainService, modelService);
    }

    public HierarchyNode createEntityWithDirection(World world, Vector3 rootPosition, Vector3 handlePosition,
                                                   String nodeName, Component... rootComponents) {
        int handleId = world.create();
        int rootId = world.create();
        int lineId = world.create();

        //todo move creation to archetypes
        world.edit(handleId)
                .add(new PositionComponent(handlePosition))
                .add(RenderComponent.of(new DirectionHandleRenderDelegate()));

        var rootEdit = world.edit(rootId)
                .add(new PositionComponent(rootPosition, handleId));
        for (var c : rootComponents) {
            rootEdit.add(c);
        }

        world.edit(lineId)
                .add(new Point2PointPositionComponent(rootId, handleId))
                .add(RenderComponent.of(new DirectionLineRenderDelegate()));

        var res = new HierarchyNode(rootId, nodeName + " " + rootId);
        res.addChild(handleId, HANDLE_NAME);
        return res;
    }
}
