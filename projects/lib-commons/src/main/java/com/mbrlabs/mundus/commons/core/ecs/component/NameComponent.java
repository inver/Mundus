package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NameComponent extends Component {

    private String name;

    public NameComponent(String name) {
        this.name = name;
    }
}
