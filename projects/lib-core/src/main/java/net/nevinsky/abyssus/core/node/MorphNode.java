package net.nevinsky.abyssus.core.node;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MorphNode extends Node {
    /**
     * null if no morph targets
     */
    private WeightVector weights;

    /**
     * optionnal morph target names (eg. exported from Blender with custom properties enabled).
     * shared with others nodes with same mesh.
     */
    private List<String> morphTargetNames;

    @Override
    public Node copy() {
        return new MorphNode().set(this);
    }

    @Override
    protected Node set(Node other) {
        if (other instanceof MorphNode) {
            if (((MorphNode) other).weights != null) {
                weights = ((MorphNode) other).weights.cpy();
                morphTargetNames = ((MorphNode) other).morphTargetNames;
            }
        }
        return super.set(other);
    }
}
