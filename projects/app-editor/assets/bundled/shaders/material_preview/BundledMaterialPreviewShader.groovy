package bundled.shaders.material_preview

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.math.Matrix4
import com.mbrlabs.mundus.commons.env.SceneEnvironment
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader
import net.nevinsky.abyssus.core.shader.ShaderConfig

class BundledMaterialPreviewShader extends DefaultShader {

    protected final int UNIFORM_MODEL_MATRIX = register(new Uniform("u_modelMatrix"))

    private final Matrix4 modelPos = new Matrix4()

    BundledMaterialPreviewShader(ShaderConfig config, Renderable renderable) {
        super(config, renderable)
    }

    @Override
    void render(Renderable renderable) {
        final SceneEnvironment env = (SceneEnvironment) renderable.environment

//        setLights(env)
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)
        set(UNIFORM_MODEL_MATRIX, modelPos)

        // texture uniform
        var diffuseTexture = ((TextureAttribute) (renderable.material.get(TextureAttribute.Diffuse)))
        var diffuseColor = ((ColorAttribute) (renderable.material.get(ColorAttribute.Diffuse)))
//
        if (diffuseTexture != null) {
//            set(UNIFORM_MATERIAL_DIFFUSE_TEXTURE, diffuseTexture.textureDescription.texture)
//            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 1)
        } else {
//            set(UNIFORM_MATERIAL_DIFFUSE_COLOR, diffuseColor.color)
//            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 0)
        }

//        // shininess
//        if (renderable.material.has(FloatAttribute.Shininess)) {
//            float shininess = ((FloatAttribute) renderable.material.get(FloatAttribute.Shininess)).value;
//            set(UNIFORM_MATERIAL_SHININESS, shininess);
//        }

        // bind attributes, bind mesh & render; then unbinds everything
        renderable.meshPart.render(program)
    }
}
