package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.graphics.GL20;
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
     * Set to 0 to disable culling
     */
    private int defaultCullFace = GL20.GL_BACK;
    /**
     * Set to 0 to disable depth test
     */
    private int defaultDepthFunc = GL20.GL_LEQUAL;

    public ShaderConfig() {
    }

    public ShaderConfig(final String vertexShader, final String fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }
}