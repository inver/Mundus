package com.mbrlabs.mundus.editor.core.ecs;

import com.artemis.Component;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.core.ecs.EcsService;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.commons.core.ecs.component.Point2PointPositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.model.ModelService;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import com.mbrlabs.mundus.editor.core.shader.ShaderConstants;
import com.mbrlabs.mundus.editor.ui.components.handle.DirectionHandleRenderDelegate;
import com.mbrlabs.mundus.editor.ui.components.handle.DirectionLineRenderDelegate;

import static com.mbrlabs.mundus.commons.scene3d.HierarchyNode.Type.NONE;

@org.springframework.stereotype.Component
public class EditorEcsService extends EcsService {

    private static final String HANDLE_NAME = "'Direction' handle";

    public EditorEcsService(AssetManager assetManager, TerrainService terrainService, ModelService modelService) {
        super(assetManager, terrainService, modelService);
    }

    @Override
    protected void configuration(WorldConfigurationBuilder builder) {
        super.configuration(builder);
    }

    public HierarchyNode createEntityWithDirection(World world, Vector3 rootPosition, Vector3 handlePosition,
                                                   String nodeName, RenderableDelegate renderableDelegate,
                                                   Component... rootComponents) {
        return createEntityWithDirection(world, rootPosition, handlePosition, NONE, nodeName, rootComponents);
    }

    public HierarchyNode createEntityWithDirection(World world, Vector3 rootPosition, Vector3 handlePosition,
                                                   HierarchyNode.Type type, String nodeName,
                                                   Component... rootComponents) {
        int handleId = world.create();
        int rootId = world.create();
        int lineId = world.create();

        //todo move creation to archetypes
        world.edit(handleId)
                .add(new PositionComponent(handlePosition))
                .add(new DirectionHandleRenderDelegate().asComponent())
                .add(PickableComponent.of(handleId, new DirectionHandleRenderDelegate(ShaderConstants.PICKER)));

        var rootEdit = world.edit(rootId)
                .add(new PositionComponent(rootPosition, handleId))
                .add(RenderComponent.of(renderableDelegate))
                .add(PickableComponent.of(rootId, renderableDelegate));
        for (var c : rootComponents) {
            rootEdit.add(c);
        }

        world.edit(lineId)
                .add(new Point2PointPositionComponent(rootId, handleId))
                .add(new DirectionLineRenderDelegate().asComponent());

        var res = new HierarchyNode(rootId, nodeName + " " + rootId, type);
        res.addChild(handleId, HANDLE_NAME);
        return res;
    }
}
