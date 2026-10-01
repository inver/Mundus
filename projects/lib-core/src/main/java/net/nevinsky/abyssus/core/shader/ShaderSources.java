package net.nevinsky.abyssus.core.shader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * The GLSL sources bundled with this library. They are the canonical version: the bundled editor shaders are copies of
 * these files.
 */
public final class ShaderSources {
    public static final String DEFAULT_VERTEX = "/shader/default.vertex.glsl";
    public static final String DEFAULT_FRAGMENT = "/shader/default.fragment.glsl";
    public static final String PBR_FRAGMENT = "/shader/pbr.fragment.glsl";

    private ShaderSources() {
    }

    public static String read(String resource) {
        try (InputStream in = ShaderSources.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Shader resource not found: " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
