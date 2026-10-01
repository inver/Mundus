package com.mbrlabs.mundus.editor.core.shader;

import net.nevinsky.abyssus.core.shader.ShaderSources;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The GLSL of the bundled editor shaders is a copy of the shaders of lib-core. Keep this table in sync with
 * {@code bundledShaderSources} in app-editor.gradle; run {@code ./gradlew :app-editor:syncBundledShaders} to fix a
 * failure.
 */
class BundledShadersInSyncTest {

    private static final Map<String, String> COPIES = new LinkedHashMap<>();

    static {
        COPIES.put("defaultShader/default.vert.glsl", ShaderSources.DEFAULT_VERTEX);
        COPIES.put("defaultShader/default.frag.glsl", ShaderSources.DEFAULT_FRAGMENT);
        COPIES.put("model/model.vert.glsl", ShaderSources.DEFAULT_VERTEX);
        COPIES.put("model/model.frag.glsl", ShaderSources.DEFAULT_FRAGMENT);
        COPIES.put("material_preview/material_preview.vert.glsl", ShaderSources.DEFAULT_VERTEX);
        COPIES.put("material_preview/material_preview.frag.glsl", ShaderSources.DEFAULT_FRAGMENT);
        COPIES.put("pbr/pbr.vert.glsl", ShaderSources.DEFAULT_VERTEX);
        COPIES.put("pbr/pbr.frag.glsl", ShaderSources.PBR_FRAGMENT);
    }

    @Test
    void bundledShadersAreCopiesOfLibCoreShaders() throws IOException {
        for (var entry : COPIES.entrySet()) {
            var resource = "/bundled/shaders/" + entry.getKey();
            try (var in = getClass().getResourceAsStream(resource)) {
                assertNotNull(in, "Bundled shader not found: " + resource);
                var bundled = IOUtils.toString(in, StandardCharsets.UTF_8);
                assertEquals(ShaderSources.read(entry.getValue()), bundled,
                        resource + " is out of date, run ./gradlew :app-editor:syncBundledShaders");
            }
        }
    }
}
