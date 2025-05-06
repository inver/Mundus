package net.nevinsky.abyssus.lib.assets.gltf.dto.animation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFPropertyDto;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * An animation sampler combines timestamps with a sequence of output values and defines an interpolation algorithm.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFAnimationSamplerDto extends GlTFPropertyDto {

    /**
     * The index of an accessor containing keyframe timestamps. The accessor **MUST** be of scalar type with
     * floating-point components. The values represent time in seconds with `time[0] >= 0.0`, and strictly increasing
     * values, i.e., `time[n + 1] > time[n]`.
     */
    private int input;

    /**
     * Interpolation algorithm.
     */
    private Interpolation interpolation = Interpolation.LINEAR;

    /**
     * The index of an accessor, containing keyframe output values.
     */
    private int output;

    @RequiredArgsConstructor
    public enum Interpolation {
        /**
         * The animated values are linearly interpolated between keyframes. When targeting a rotation, spherical linear
         * interpolation (slerp) **SHOULD** be used to interpolate quaternions. The number of output elements **MUST**
         * equal the number of input elements.
         */
        LINEAR,
        /**
         * The animated values remain constant to the output of the first keyframe, until the next keyframe. The number
         * of output elements **MUST** equal the number of input elements.
         */
        STEP,
        /**
         * The animation's interpolation is computed using a cubic spline with specified tangents. The number of output
         * elements **MUST** equal three times the number of input elements. For each input element, the output stores
         * three elements, an in-tangent, a spline vertex, and an out-tangent. There **MUST** be at least two keyframes
         * when using this interpolation.
         */
        CUBICSPLINE;

        @JsonValue
        public String toValue() {
            return name();
        }

        @JsonCreator
        public static Interpolation from(String inputValue) {
            return Arrays.stream(values())
                    .filter(v -> StringUtils.equalsIgnoreCase(v.name(), inputValue))
                    .findAny()
                    .orElse(null);
        }
    }
}
