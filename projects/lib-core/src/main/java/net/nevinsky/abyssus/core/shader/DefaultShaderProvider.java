package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.files.FileHandle;
import lombok.Getter;
import net.nevinsky.abyssus.core.Renderable;

public class DefaultShaderProvider extends AbstractShaderProvider<ShaderHolder> {
    @Getter
    private final ShaderConfig config;

    public DefaultShaderProvider(final ShaderConfig config) {
        this.config = (config == null) ? new ShaderConfig() : config;
    }

    public DefaultShaderProvider(final String vertexShader, final String fragmentShader) {
        this(new ShaderConfig(vertexShader, fragmentShader));
    }

    public DefaultShaderProvider(final FileHandle vertexShader, final FileHandle fragmentShader) {
        this(vertexShader.readString(), fragmentShader.readString());
    }

    public DefaultShaderProvider() {
        this(null);
    }

    @Override
    public Shader get(String key, Renderable renderable) {
        var res = super.get(key, renderable);
        if (res != null) {
            return res;
        }

        res = shaderCache.get(DEFAULT_SHADER_KEY).getForRenderable(renderable);
        if (res != null) {
            return res;
        }

        throw new RuntimeException("Could not find shader for renderable. Even default shader doesn't accept it.");
    }

    @Override
    protected Shader createShader(ShaderHolder holder, Renderable renderable) {
        return new DefaultShader(config.getVertexShader(), config.getFragmentShader());
    }
}
