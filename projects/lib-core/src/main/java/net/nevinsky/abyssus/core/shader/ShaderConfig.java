package net.nevinsky.abyssus.core.shader;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShaderConfig {
    /**
     * The uber vertex shader to use, null to use the default vertex shader.
     */
    private String vertexShader = null;
    /**
     * The uber fragment shader to use, null to use the default fragment shader.
     */
    private String fragmentShader = null;
    /**
     * The number of directional lights to use
     */
    private int numDirectionalLights = 2;
    /**
     * The number of point lights to use
     */
    private int numPointLights = 5;
    /**
     * The number of spotlights to use
     */
    private int numSpotLights = 0;
    /**
     * The number of bones to use
     */
    private int numBones = 12;
    /**
     *
     */
    private boolean ignoreUnimplemented = true;
    /**
     * Set to 0 to disable culling, -1 to inherit from {@link OldDefaultShader#defaultCullFace}
     */
    private int defaultCullFace = -1;
    /**
     * Set to 0 to disable depth test, -1 to inherit from {@link OldDefaultShader#defaultDepthFunc}
     */
    private int defaultDepthFunc = -1;

    public ShaderConfig() {
    }

    public ShaderConfig(final String vertexShader, final String fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }
}