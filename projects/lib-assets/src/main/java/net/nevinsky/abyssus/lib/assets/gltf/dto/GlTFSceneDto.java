package net.nevinsky.abyssus.lib.assets.gltf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The root nodes of a scene.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFSceneDto extends GlTFChildOfRootPropertyDto {
    /**
     * The indices of each root node.
     */
    private List<Integer> nodes = new ArrayList<>();
}
