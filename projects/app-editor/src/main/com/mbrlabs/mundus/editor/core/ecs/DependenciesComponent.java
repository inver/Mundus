package com.mbrlabs.mundus.editor.core.ecs;

import com.artemis.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class DependenciesComponent extends Component {
    @Setter
    private List<Integer> dependencies = new ArrayList<>();

    public DependenciesComponent(int... dependencies) {
        for (int d : dependencies) {
            this.dependencies.add(d);
        }
    }
}
