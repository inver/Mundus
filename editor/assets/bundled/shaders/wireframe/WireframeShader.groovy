package bundled.shaders.wireframe

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.mbrlabs.mundus.editor.utils.GlUtils
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader
import net.nevinsky.abyssus.core.shader.Shader

class WireframeShader extends DefaultShader {

    private def UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"))
    private def UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"))

    WireframeShader(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader)
    }

    @Override
    int compareTo(Shader other) {
        return 0
    }

    @Override
    boolean canRender(Renderable instance) {
        return true
    }

    @Override
    void begin(Camera camera, RenderContext context) {
        this.context = context
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)

        program.bind()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
    }

    @Override
    void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)
        GlUtils.Unsafe.polygonModeWireframe()

        renderable.meshPart.render(program)
    }

    @Override
    void end() {
        GlUtils.Unsafe.polygonModeFill()
    }

    @Override
    void dispose() {
        program.dispose()
    }

}
