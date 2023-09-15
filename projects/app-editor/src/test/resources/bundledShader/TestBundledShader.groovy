package bundledShader

import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader
import net.nevinsky.abyssus.core.shader.ShaderConfig

class TestBundledShader extends DefaultShader {

    TestBundledShader(ShaderConfig config, Renderable renderable) {
        super(config, renderable)
    }
}