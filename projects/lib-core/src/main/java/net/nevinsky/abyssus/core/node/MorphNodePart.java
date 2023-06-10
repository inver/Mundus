package net.nevinsky.abyssus.core.node;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.core.Renderable;

/**
 * NodePart with stored morph target
 */
@NoArgsConstructor
@Getter
@Setter
public class MorphNodePart extends NodePart {
    private WeightVector morphTargets;

    public Renderable setRenderable(final Renderable out) {
        out.material = material;
        out.meshPart.set(meshPart);
        out.bones = bones;
        out.userData = morphTargets;
        return out;
    }

    @Override
    public NodePart copy() {
        return new MorphNodePart().set(this);
    }

    @Override
    protected NodePart set(NodePart other) {
        super.set(other);
        if (other instanceof MorphNodePart) {
            morphTargets = ((MorphNodePart) other).morphTargets;
        }
        return this;
    }
}
