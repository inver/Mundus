package net.nevinsky.abyssus.lib.assets.gltf.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Metadata about the glTF asset.
 */
@Getter
public class GlTFAssetDto {
    /**
     * A copyright message suitable for display to credit the content creator.
     */
    private final String copyright;
    /**
     * Tool that generated this glTF model.  Useful for debugging.
     */
    private final String generator;
    /**
     * The glTF version in the form of `<major>.<minor>` that this asset targets.
     * <p>
     * pattern: ^[0-9]+\\.[0-9]+$
     */
    private final String version;
    /**
     * The minimum glTF version in the form of `<major>.<minor>` that this asset targets. This property **MUST NOT** be
     * greater than the asset version.
     * <p>
     * pattern: ^[0-9]+\\.[0-9]+$
     */
    private final String minVersion;

    @JsonCreator
    public GlTFAssetDto(@JsonProperty("copyright") String copyright, @JsonProperty("generator") String generator,
                        @JsonProperty("version") String version, @JsonProperty("minVersion") String minVersion) {
        this.copyright = copyright;
        this.generator = generator;
        this.version = version;
        this.minVersion = minVersion;
    }

    public static GlTFAssetDto INSTANCE = new GlTFAssetDto("", "Abyssus GlTF generator", "0.1", "0.1");
}
