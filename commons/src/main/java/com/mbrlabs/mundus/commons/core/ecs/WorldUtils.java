package com.mbrlabs.mundus.commons.core.ecs;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public final class WorldUtils {
    public static <R, I extends Component> List<R> getFromWorld(World world, Class<I> clazz, BiFunction<Integer, I, R> converter) {
        var entityIds = world.getAspectSubscriptionManager().get(Aspect.all(clazz)).getEntities();
        if (entityIds.isEmpty()) {
            return Collections.emptyList();
        }
        var mapper = world.getMapper(clazz);

        var res = new ArrayList<R>();
        for (int i = 0; i < entityIds.size(); i++) {
            var entityId = entityIds.get(i);
            res.add(converter.apply(entityId, mapper.get(entityId)));
        }
        return res;
    }
}
