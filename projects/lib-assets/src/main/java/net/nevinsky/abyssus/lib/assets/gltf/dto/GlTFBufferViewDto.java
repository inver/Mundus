package net.nevinsky.abyssus.lib.assets.gltf.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

/**
 * A view into a buffer generally representing a subset of the buffer.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFBufferViewDto extends GlTFChildOfRootPropertyDto {

    /**
     * The index of the buffer.
     */
    private int buffer;
    /**
     * The offset into the buffer in bytes.
     */
    private Integer byteOffset = null;
    /**
     * The length of the bufferView in bytes.
     */
    private int byteLength;
    /**
     * The stride, in bytes, between vertex attributes.  When this is not defined, data is tightly packed. When two or
     * more accessors use the same buffer view, this field **MUST** be defined.
     */
    private Integer byteStride = null;
    /**
     * The hint representing the intended GPU buffer type to use with this buffer view.
     */
    private Target target;

    @RequiredArgsConstructor
    public enum Target {
        ARRAY_BUFFER(34962),
        ELEMENT_ARRAY_BUFFER(34963);

        private final int value;

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Target from(int inputValue) {
            return Arrays.stream(values())
                    .filter(v -> v.value == inputValue)
                    .findAny()
                    .orElse(null);
        }
    }
}
