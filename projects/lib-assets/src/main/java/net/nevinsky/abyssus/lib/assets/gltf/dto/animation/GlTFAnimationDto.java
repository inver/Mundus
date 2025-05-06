package net.nevinsky.abyssus.lib.assets.gltf.dto.animation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFChildOfRootPropertyDto;

import java.util.ArrayList;
import java.util.List;

/**
 * A keyframe animation.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFAnimationDto extends GlTFChildOfRootPropertyDto {

    /**
     * An array of animation channels. An animation channel combines an animation sampler with a target property being
     * animated. Different channels of the same animation **MUST NOT** have the same targets.
     */
    private List<GlTFAnimationChannelDto> channels = new ArrayList<>();

    /**
     * An array of animation samplers. An animation sampler combines timestamps with a sequence of output values and
     * defines an interpolation algorithm.
     */
    private List<GlTFAnimationSamplerDto> samplers = new ArrayList<>();
}
