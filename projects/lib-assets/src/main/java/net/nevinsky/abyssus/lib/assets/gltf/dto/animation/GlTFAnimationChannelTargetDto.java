package net.nevinsky.abyssus.lib.assets.gltf.dto.animation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * The descriptor of the animated property.
 */
@NoArgsConstructor
@Getter
@Setter
public class GlTFAnimationChannelTargetDto {
    /**
     * The index of the node to animate. When undefined, the animated object **MAY** be defined by an extension.
     */
    private int node;

    private Path path;

    @RequiredArgsConstructor
    public enum Path {
        TRANSLATION,
        ROTATION,
        SCALE,
        WEIGHTS;

        @JsonValue
        public String toValue() {
            return name().toLowerCase();
        }

        @JsonCreator
        public static Path from(String inputValue) {
            return Arrays.stream(values())
                    .filter(v -> StringUtils.equalsIgnoreCase(v.name(), inputValue))
                    .findAny()
                    .orElse(null);
        }
    }
}
