package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class ParentComponent extends Component {

    @Setter
    private int parentEntityId = -1;

    public ParentComponent(int parentEntityId) {
        this.parentEntityId = parentEntityId;
    }
}
