package net.nevinsky.abyssus.lib.assets.gltf.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.GlTFException;

import java.util.Arrays;
import java.util.Objects;

/**
 * A typed view into a buffer view that contains raw binary data.
 */
@NoArgsConstructor
@Setter
@Getter
public class GlTFAccessorDto extends GlTFChildOfRootPropertyDto {
    /**
     * The index of the buffer view. When undefined, the accessor **MUST** be initialized with zeros; `sparse` property
     * or extensions **MAY** override zeros with actual values.
     */
    private Integer bufferView;
    /**
     * The offset relative to the start of the buffer view in bytes.  This **MUST** be a multiple of the size of the
     * component datatype. This property **MUST NOT** be defined when `bufferView` is undefined.
     */
    private Integer byteOffset = null;
    /**
     * The datatype of the accessor's components.  UNSIGNED_INT type **MUST NOT** be used for any accessor that is not
     * referenced by `mesh.primitive.indices`.
     */
    private ComponentType componentType;
    /**
     * Specifies whether integer data values are normalized (`true`) to [0, 1] (for unsigned types) or to [-1, 1]
     * (for signed types) when they are accessed. This property **MUST NOT** be set to `true` for accessors with
     * `FLOAT` or `UNSIGNED_INT` component type.
     */
    private Boolean normalized = null;
    /**
     * The number of elements referenced by this accessor, not to be confused with the number of bytes or number of
     * components.
     */
    private int count;
    /**
     * Specifies if the accessor's elements are scalars, vectors, or matrices.
     */
    private Type type;
    /**
     * Maximum value of each component in this accessor.  Array elements **MUST** be treated as having the same data
     * type as accessor's `componentType`. Both `min` and `max` arrays have the same length.  The length is determined
     * by the value of the `type` property; it can be 1, 2, 3, 4, 9, or 16.
     * <p>
     * `normalized` property has no effect on array values: they always correspond to the actual values stored in the
     * buffer. When the accessor is sparse, this property **MUST** contain maximum values of accessor data with sparse
     * substitution applied.
     */
    private float[] max;
    /**
     * Minimum value of each component in this accessor.  Array elements **MUST** be treated as having the same data
     * type as accessor's `componentType`. Both `min` and `max` arrays have the same length.  The length is determined
     * by the value of the `type` property; it can be 1, 2, 3, 4, 9, or 16.
     * <p>
     * `normalized` property has no effect on array values: they always correspond to the actual values stored in the
     * buffer. When the accessor is sparse, this property **MUST** contain minimum values of accessor data with sparse
     * substitution applied.
     */
    private float[] min;
    /**
     * Sparse storage of elements that deviate from their initialization value.
     */
    private GltfSparseDto sparse;

    @JsonIgnore
    public int getStrideSize() {
        return getTypeSize() * getComponentTypeSize();
    }

    @JsonIgnore
    public int getSize() {
        return getStrideSize() * count;
    }

    private int getTypeSize() {
        switch (type) {
            case SCALAR:
                return 1;
            case VEC2:
                return 2;
            case VEC3:
                return 3;
            case VEC4:
            case MAT2:
                return 4;
            case MAT3:
                return 9;
            case MAT4:
                return 16;
        }
        throw new GlTFException("illegal accessor type: " + type);
    }

    private int getComponentTypeSize() {
        switch (componentType) {
            case UNSIGNED_BYTE:
            case BYTE:
                return 1;
            case SHORT:
            case UNSIGNED_SHORT:
                return 2;
            case UNSIGNED_INT:
            case FLOAT:
                return 4;
        }
        throw new GlTFException("illegal accessor component type: " + componentType);
    }

    @RequiredArgsConstructor
    public enum ComponentType {
        BYTE(5120),
        UNSIGNED_BYTE(5121),
        SHORT(5122),
        UNSIGNED_SHORT(5123),
        UNSIGNED_INT(5125),
        FLOAT(5126);

        private final int value;

        @JsonValue
        public int toValue() {
            return value;
        }

        @JsonCreator
        public static ComponentType from(int inputValue) {
            return Arrays.stream(values())
                    .filter(v -> v.value == inputValue)
                    .findAny()
                    .orElse(null);
        }
    }

    @RequiredArgsConstructor
    public enum Type {
        SCALAR,
        VEC2,
        VEC3,
        VEC4,
        MAT2,
        MAT3,
        MAT4;

        @JsonValue
        public String toValue() {
            return name();
        }

        @JsonCreator
        public static Type from(String inputValue) {
            return Arrays.stream(values())
                    .filter(v -> Objects.equals(v.name(), inputValue))
                    .findAny()
                    .orElse(null);
        }
    }
}
