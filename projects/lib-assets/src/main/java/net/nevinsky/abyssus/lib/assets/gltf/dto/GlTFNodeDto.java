package net.nevinsky.abyssus.lib.assets.gltf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Node
 * <p>
 * A node in the node hierarchy.  When the node contains `skin`, all `mesh.primitives` **MUST** contain `JOINTS_0` and
 * `WEIGHTS_0` attributes.  A node **MAY** have either a `matrix` or any combination of `translation`/`rotation`/`scale`
 * (TRS) properties. TRS properties are converted to matrices and postmultiplied in the `T * R * S` order to compose the
 * transformation matrix; first the scale is applied to the vertices, then the rotation, and then the translation. If
 * none are provided, the transform is the identity. When a node is targeted for animation
 * (referenced by an animation.channel.target), `matrix` **MUST NOT** be present.
 * <p>
 * Required fields:
 * "anyOf": [
 * { "required": [ "matrix", "translation" ] },
 * { "required": [ "matrix", "rotation" ] },
 * { "required": [ "matrix", "scale" ] }
 * ]
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFNodeDto extends GlTFChildOfRootPropertyDto {
    /**
     * The index of the camera referenced by this node.
     */
    private Integer camera = null;

    /**
     * The indices of this node's children.
     */
    private final List<Integer> children = new ArrayList<>();
    /**
     * The index of the skin referenced by this node. When a skin is referenced by a node within a scene, all joints
     * used by the skin **MUST** belong to the same scene. When defined, `mesh` **MUST** also be defined.
     */
    private Integer skin = null;

    /**
     * A floating-point 4x4 transformation matrix stored in column-major order.
     */
    private float[] matrix;

    /**
     * The index of the mesh in this node.
     */
    private Integer mesh = null;

    /**
     * The node's unit quaternion rotation in the order (x, y, z, w), where w is the scalar.
     */
    private float[] rotation;

    /**
     * The node's non-uniform scale, given as the scaling factors along the x, y, and z axes.
     */
    private float[] scale;

    /**
     * The node's translation along the x, y, and z axes.
     */
    private float[] translation;
    /**
     * The weights of the instantiated morph target. The number of array elements **MUST** match the number of morph
     * targets of the referenced mesh. When defined, `mesh` **MUST** also be defined.
     * <p>
     * Minimum count of items = 1.
     */
    private final List<Float> weights = new ArrayList<>();
}
