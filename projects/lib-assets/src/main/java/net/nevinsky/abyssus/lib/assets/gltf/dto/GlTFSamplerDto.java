package net.nevinsky.abyssus.lib.assets.gltf.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

/**
 * Texture sampler properties for filtering and wrapping modes.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFSamplerDto {
    /**
     * Magnification filter.
     */
    private MagFilter magFilter;
    /**
     * Minification filter.
     */
    private MinFilter minFilter;
    /**
     * S (U) wrapping mode.  All valid values correspond to WebGL enums.
     */
    private WrapMode wrapS;
    /**
     * T (V) wrapping mode.
     */
    private WrapMode wrapT;

    @RequiredArgsConstructor
    public enum MagFilter {
        NEAREST(9728),
        LINEAR(9729);

        @Getter
        @JsonValue
        private final int value;

        @JsonCreator
        public static MagFilter from(int inputValue) {
            return Arrays.stream(values())
                    .filter(v -> v.value == inputValue)
                    .findAny()
                    .orElse(null);
        }
    }

    @RequiredArgsConstructor
    public enum MinFilter {
        NEAREST(9728),
        LINEAR(9729),
        NEAREST_MIPMAP_NEAREST(9984),
        LINEAR_MIPMAP_NEAREST(9985),
        NEAREST_MIPMAP_LINEAR(9986),
        LINEAR_MIPMAP_LINEAR(9987);

        @JsonValue
        @Getter
        private final int value;

        @JsonCreator
        public static MinFilter from(int inputValue) {
            return Arrays.stream(values())
                    .filter(v -> v.value == inputValue)
                    .findAny()
                    .orElse(null);
        }
    }

    @RequiredArgsConstructor
    public enum WrapMode {
        CLAMP_TO_EDGE(33071),
        MIRRORED_REPEAT(33648),
        REPEAT(10497);

        @Getter
        @JsonValue
        private final int value;

        @JsonCreator
        public static WrapMode from(int inputValue) {
            return Arrays.stream(values())
                    .filter(v -> v.value == inputValue)
                    .findAny()
                    .orElse(null);
        }
    }
}
