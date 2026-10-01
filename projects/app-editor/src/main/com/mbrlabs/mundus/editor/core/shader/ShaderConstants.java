package com.mbrlabs.mundus.editor.core.shader;

import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

/**
 * Class holds default shader names
 */
public final class ShaderConstants {
    private ShaderConstants() {
    }

    public static final String WIREFRAME = "wireframe";
    public static final String TERRAIN = "terrain";
    public static final String MODEL = "model";
    public static final String SKYBOX = "skyboxShader";
    public static final String PICKER = "picker";
    public static final String PBR = "pbr";

    /**
     * @return the key of the shader to render the model: PBR for models with PBR materials, default otherwise
     */
    public static String forModel(Model model) {
        return model.hasPbrMaterials() ? PBR : ShaderProvider.DEFAULT_SHADER_KEY;
    }

}