package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.nevinsky.abyssus.core.Renderable;

/**
 * Metallic-roughness shader. It reads base color, normal and emissive from the same attributes as the
 * {@link DefaultShader} (a PBR material keeps both) and adds the metallic and roughness factors and the combined
 * metallic-roughness and occlusion textures.
 */
public class PbrShader extends DefaultShader {

    protected int u_metallicFactor;
    protected int u_roughnessFactor;
    protected int u_metallicRoughnessTexture;
    protected int u_metallicRoughnessUVTransform;
    protected int u_occlusionTexture;
    protected int u_occlusionUVTransform;

    /**
     * @param config the vertex shader defaults to the default vertex shader, the fragment shader to the PBR one
     */
    public PbrShader(ShaderConfig config, Renderable renderable) {
        super(withDefaults(config), renderable);

        u_metallicFactor = registerUniformLocal("u_metallicFactor", PBRFloatAttribute.Metallic,
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID,
                        ((FloatAttribute) combinedAttributes.get(PBRFloatAttribute.Metallic)).value));
        u_roughnessFactor = registerUniformLocal("u_roughnessFactor", PBRFloatAttribute.Roughness,
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID,
                        ((FloatAttribute) combinedAttributes.get(PBRFloatAttribute.Roughness)).value));

        u_metallicRoughnessTexture = registerTexture("u_metallicRoughnessTexture",
                PBRTextureAttribute.MetallicRoughnessTexture);
        u_metallicRoughnessUVTransform = registerUvTransform("u_metallicRoughnessUVTransform",
                PBRTextureAttribute.MetallicRoughnessTexture);
        u_occlusionTexture = registerTexture("u_occlusionTexture", PBRTextureAttribute.OcclusionTexture);
        u_occlusionUVTransform = registerUvTransform("u_occlusionUVTransform", PBRTextureAttribute.OcclusionTexture);
    }

    /**
     * @return {@code true} if the renderable has a PBR material (metallic or roughness set), so this shader is the
     * right one
     */
    public static boolean isPbr(Renderable renderable) {
        if (renderable.material == null) {
            return false;
        }
        long mask = renderable.material.getMask();
        return (mask & (PBRFloatAttribute.Metallic | PBRFloatAttribute.Roughness)) != 0;
    }

    private static ShaderConfig withDefaults(ShaderConfig config) {
        var copy = config.copy();
        if (copy.getVertexShader() == null) {
            copy.setVertexShader(ShaderSources.read(ShaderSources.DEFAULT_VERTEX));
        }
        if (copy.getFragmentShader() == null) {
            copy.setFragmentShader(ShaderSources.read(ShaderSources.PBR_FRAGMENT));
        }
        return copy;
    }

    private int registerTexture(String alias, long attribute) {
        return registerUniformLocal(alias, attribute,
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID,
                        shader.context.textureBinder.bind(
                                ((TextureAttribute) combinedAttributes.get(attribute)).textureDescription)));
    }

    private int registerUvTransform(String alias, long attribute) {
        return registerUniformLocal(alias, attribute, (shader, inputID, renderable1, combinedAttributes) -> {
            var ta = (TextureAttribute) combinedAttributes.get(attribute);
            shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
        });
    }

    @Override
    protected void preprocessShaderContents(Renderable renderable) {
        super.preprocessShaderContents(renderable);

        long mask = renderable.material == null ? 0 : renderable.material.getMask();
        var sb = new StringBuilder();
        if ((mask & PBRFloatAttribute.Metallic) == PBRFloatAttribute.Metallic) {
            sb.append("#define metallicFactorFlag\n");
        }
        if ((mask & PBRFloatAttribute.Roughness) == PBRFloatAttribute.Roughness) {
            sb.append("#define roughnessFactorFlag\n");
        }
        if ((mask & PBRTextureAttribute.MetallicRoughnessTexture) == PBRTextureAttribute.MetallicRoughnessTexture) {
            sb.append("#define metallicRoughnessTextureFlag\n");
        }
        if ((mask & PBRTextureAttribute.OcclusionTexture) == PBRTextureAttribute.OcclusionTexture) {
            sb.append("#define occlusionTextureFlag\n");
        }
        vertexShader = sb + vertexShader;
        fragmentShader = sb + fragmentShader;
    }
}
