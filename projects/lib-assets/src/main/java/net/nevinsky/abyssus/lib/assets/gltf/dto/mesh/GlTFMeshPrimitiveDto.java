package net.nevinsky.abyssus.lib.assets.gltf.dto.mesh;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A set of primitives to be rendered.  Its global transform is defined by a node that references it.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFMeshPrimitiveDto {
    /**
     * A plain JSON object, where each key corresponds to a mesh attribute semantic and each value is the index of the
     * accessor containing attribute's data.
     */
    private Map<String, Integer> attributes = new HashMap<>();

    /**
     * The index of the accessor that contains the vertex indices.  When this is undefined, the primitive defines
     * non-indexed geometry.  When defined, the accessor **MUST** have `SCALAR` type and an unsigned integer component
     * type.
     */
    private int indices;

    /**
     * The index of the material to apply to this primitive when rendering.
     */
    private int material;

    /**
     * The topology type of primitives to render.
     */
    private Mode mode;

    /**
     * A plain JSON object specifying attributes displacements in a morph target, where each key corresponds to one of
     * the three supported attribute semantic (`POSITION`, `NORMAL`, or `TANGENT`) and each value is the index of the
     * accessor containing the attribute displacements' data.
     */
    private List<Map<String, Integer>> targets = new ArrayList<>();

    @RequiredArgsConstructor
    public enum Mode {
        POINTS(0),
        LINES(1),
        LINE_LOOP(2),
        LINE_STRIP(3),
        TRIANGLES(4),
        TRIANGLE_STRIP(5),
        TRIANGLE_FAN(6);

        private final int value;

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static Mode from(int inputValue) {
            return Arrays.stream(values())
                    .filter(v -> v.value == inputValue)
                    .findAny()
                    .orElse(null);
        }
    }
}
