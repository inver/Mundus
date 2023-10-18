package net.nevinsky.abyssus.lib.assets.gltf.dto.animation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFPropertyDto;

/**
 * An animation channel combines an animation sampler with a target property being animated.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFAnimationChannelDto extends GlTFPropertyDto {
    /**
     * The index of a sampler in this animation used to compute the value for the target, e.g., a node's translation,
     * rotation, or scale (TRS).
     */
    private int sampler;

    /**
     * The descriptor of the animated property.
     */
    private GlTFAnimationChannelTargetDto target;
}
