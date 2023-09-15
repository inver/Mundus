package bundled.shaders.skyboxShader

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.math.Matrix4
import com.mbrlabs.mundus.commons.env.SceneEnvironment
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader
import net.nevinsky.abyssus.core.shader.ShaderConfig

class BundledSkyboxShader extends DefaultShader {

    private final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"))
    private final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"))
    private final int UNIFORM_TEXTURE = register(new Uniform("u_texture"))
    private final int UNIFORM_FOG = register(new Uniform("u_fog"))
    private final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"))

    private final Matrix4 transform = new Matrix4()

    BundledSkyboxShader(ShaderConfig config, Renderable renderable) {
        super(config, renderable)
    }

    @Override
    void begin(Camera camera, RenderContext context) {
        this.context = context
        context.begin()
        program.bind()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
        transform.idt()
        transform.translate(camera.position)
        set(UNIFORM_TRANS_MATRIX, transform)
    }

    @Override
    void render(Renderable renderable) {
        def cubemapAttribute = renderable.material.get(CubemapAttribute.EnvironmentMap)
        if (cubemapAttribute) {
            set(UNIFORM_TEXTURE, ((CubemapAttribute) cubemapAttribute).textureDescription)
        }

        def fog = ((SceneEnvironment) renderable.environment).getFog()
        if (fog == null) {
            set(UNIFORM_FOG, 0)
        } else {
            set(UNIFORM_FOG, 1)
            set(UNIFORM_FOG_COLOR, fog.color)
        }

        renderable.meshPart.render(program)
    }
}