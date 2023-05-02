package bundled.shaders.picker

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.math.Vector3
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader

/**
 * Used to render game objects in only one color.
 * <p>
 * This color represents the encoded id of the game object. By rendering with this shader on a framebuffer object one
 * can implement raypicking. The class GameObjectPicker does exactly that.
 * <p>
 * See also: http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/
 *
 * @author Marcus Brummer
 * @version 20-02-2016
 */
class PickerShader extends DefaultShader {

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"))
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"))
    protected final int UNIFORM_COLOR = register(new Uniform("u_color"))

    private static final Vector3 vec3 = new Vector3()

    PickerShader(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader)
    }

    @Override
    int compareTo(net.nevinsky.abyssus.core.shader.Shader other) {
        return 0
    }

    @Override
    boolean canRender(Renderable instance) {
        return true
    }

    @Override
    void begin(Camera camera, RenderContext context) {
        this.context = context
        this.context.setCullFace(GL20.GL_BACK)
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)

        program.bind()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
    }

    @Override
    void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)

        PickerIDAttribute goID = (PickerIDAttribute) renderable.material.get(PickerIDAttribute.TYPE)
        if (goID != null) {
            set(UNIFORM_COLOR, vec3.set(goID.r, goID.g, goID.b))
        }

        renderable.meshPart.render(program)
    }

    @Override
    void end() {
        program.end()
    }

    @Override
    void dispose() {
        program.dispose()
    }

}
