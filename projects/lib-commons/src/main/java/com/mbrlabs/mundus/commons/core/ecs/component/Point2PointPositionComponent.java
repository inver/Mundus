package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Point2PointPositionComponent extends Component {
    private int entity1Id = -1;
    private int entity2Id = -1;

    private final Vector3 point1 = new Vector3();
    private final Vector3 point2 = new Vector3();

    public Point2PointPositionComponent(int entity1Id, int entity2Id) {
        this.entity1Id = entity1Id;
        this.entity2Id = entity2Id;
    }
}
