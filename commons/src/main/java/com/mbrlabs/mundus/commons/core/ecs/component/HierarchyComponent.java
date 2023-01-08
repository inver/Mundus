package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HierarchyComponent extends Component {
    private String name;
    @EntityId
    private int entityId;
}
