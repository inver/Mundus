package bundled.shaders.picker


import com.badlogic.gdx.graphics.g3d.Attributes
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
@SuppressWarnings("unused")
class BundledPickerShader extends DefaultShader {

    private final int UNIFORM_COLOR = register(new Uniform(PickerIDAttribute.Alias))

    BundledPickerShader(ShaderConfig config, Renderable renderable) {
        super(config, renderable)
    }

//    @Override
//    void begin(Camera camera, RenderContext context) {
//        super.begin(camera, context)
////        context.setCullFace(GL20.GL_BACK)
////        context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
////        context.setDepthMask(true)
//    }
    @Override
    void render(Renderable renderable, Attributes combinedAttributes) {
        def pickerAttr = combinedAttributes.get(PickerIDAttribute.TYPE) as PickerIDAttribute
        if (pickerAttr != null) {
            set(UNIFORM_COLOR, pickerAttr.asColor())
        }
        super.render(renderable, combinedAttributes)
    }

    @Override
    void render(Renderable renderable) {
//        def pickerId = renderable.material.get(PickerIDAttribute.TYPE)
//        if (pickerId != null) {
//            set(UNIFORM_COLOR, VEC.set(pickerId.r, pickerId.g, pickerId.b))
//        }

        super.render(renderable)
    }
}
