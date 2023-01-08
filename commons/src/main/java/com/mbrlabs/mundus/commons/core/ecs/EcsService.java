package com.mbrlabs.mundus.commons.core.ecs;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.WorldSerializationManager;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.core.ecs.behavior.LookAtSystem;
import com.mbrlabs.mundus.commons.core.ecs.behavior.RenderComponentSystem;
import com.mbrlabs.mundus.commons.core.ecs.behavior.SynchronizeRenderComponentSystem;
import com.mbrlabs.mundus.commons.core.ecs.behavior.SynchronizeRenderPoint2PointSystem;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import com.mbrlabs.mundus.commons.core.ecs.serialization.RenderableObjectDelegateSerializer;
import com.mbrlabs.mundus.commons.core.ecs.serialization.RenderableObjectSerializer;
import com.mbrlabs.mundus.commons.terrain.Terrain;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class EcsService {

    private final AssetManager assetManager;
    private final TerrainService terrainService;

    public World createWorld() {
        var serializationManager = new WorldSerializationManager();

        var config = new WorldConfigurationBuilder()
                .with(serializationManager)
                .with(new EntityLinkManager())
                .with(new LookAtSystem())
                .with(new SynchronizeRenderComponentSystem())
                .with(new SynchronizeRenderPoint2PointSystem())
                .with(new RenderComponentSystem())
                .build();
        var world = new World(config);

        var serializer = new JsonArtemisSerializer(world);
        getSerializers().forEach(p -> serializer.register(p.getKey(), p.getValue()));
        serializationManager.setSerializer(serializer);
        return world;
    }

    public List<Pair<Class<?>, JsonSerializer>> getSerializers() {
        return Arrays.asList(
                Pair.of(RenderableObjectDelegate.class, new RenderableObjectDelegateSerializer(
                        assetManager, terrainService
                )),
                Pair.of(Terrain.class, new RenderableObjectSerializer())
        );
    }
}
