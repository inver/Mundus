package com.mbrlabs.mundus.commons.core.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class PositionComponent extends Component {

    @Getter
    private final Vector3 localPosition = new Vector3();
    @Getter
    private final Quaternion localRotation = new Quaternion();
    @Getter
    private final Vector3 localScale = new Vector3(1, 1, 1);

    private transient final Matrix4 combined = new Matrix4();

    @Getter
    @Setter
    @EntityId
    public int lookAtId = -1;

    public PositionComponent(int lookAtId) {
        this(Vector3.Zero, lookAtId);
    }

    public PositionComponent(Vector3 position, int lookAtId) {
        localPosition.set(position);
        this.lookAtId = lookAtId;
    }

    public PositionComponent(Vector3 position) {
        this(position, -1);
    }

    public PositionComponent(float x, float y, float z) {
        localPosition.set(x, y, z);
    }

    public Matrix4 getTransform() {
        combined.set(localPosition, localRotation, localScale);
        return combined;
    }

    public Vector3 getLocalPosition(Vector3 out) {
        return out.set(localPosition);
    }

    public Vector3 getPosition(Vector3 out) {
        return getTransform().getTranslation(out);
    }

    public void translate(Vector3 v) {
        localPosition.add(v);
    }

    public void translate(float x, float y, float z) {
        localPosition.add(x, y, z);
    }
}
