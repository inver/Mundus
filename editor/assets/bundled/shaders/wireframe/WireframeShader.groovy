package bundled.shaders.wireframe

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.mbrlabs.mundus.editor.utils.GlUtils
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader

class WireframeShader extends DefaultShader {

    WireframeShader(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader)
    }

    @Override
    void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)
    }

    @Override
    void render(Renderable renderable) {
        GlUtils.Unsafe.polygonModeWireframe()

        super.render(renderable)
    }

    @Override
    void end() {
        GlUtils.Unsafe.polygonModeFill()
        super.end()
    }
}
