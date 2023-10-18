package net.nevinsky.abyssus.lib.assets.gltf.dto.mesh;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFChildOfRootPropertyDto;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of primitives to be rendered.  Its global transform is defined by a node that references it.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFMeshDto extends GlTFChildOfRootPropertyDto {

    /**
     * An array of primitives, each defining geometry to be rendered.
     */
    private List<GlTFMeshPrimitiveDto> primitives = new ArrayList<>();
    /**
     * Array of weights to be applied to the morph targets. The number of array elements **MUST** match the number of
     * morph targets.
     */
    private float[] weights;
}
