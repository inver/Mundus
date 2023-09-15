package bundled.shaders.picker

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.math.Vector3
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader
import net.nevinsky.abyssus.core.shader.ShaderConfig

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
class BundledPickerShader extends DefaultShader {

    protected final int UNIFORM_COLOR = register(new Uniform("u_color"))

    private static final Vector3 vec3 = new Vector3()

    BundledPickerShader(ShaderConfig config, Renderable renderable) {
        super(config, renderable)
    }

    @Override
    void begin(Camera camera, RenderContext context) {
        super.begin(camera, context)
        this.context.setCullFace(GL20.GL_BACK)
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)
    }

    @Override
    void render(Renderable renderable) {
        def goID = renderable.material.get(PickerIDAttribute.TYPE)
        if (goID != null) {
            set(UNIFORM_COLOR, vec3.set(goID.r, goID.g, goID.b))
        }

        super.render(renderable)
    }
}
