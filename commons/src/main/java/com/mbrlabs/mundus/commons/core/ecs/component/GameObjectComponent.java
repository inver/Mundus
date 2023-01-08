package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.mbrlabs.mundus.commons.core.ecs.base.HasEntityId;
import lombok.Getter;

public class GameObjectComponent extends Component implements HasEntityId {
    @EntityId
    @Getter
    public int entityId;
}
