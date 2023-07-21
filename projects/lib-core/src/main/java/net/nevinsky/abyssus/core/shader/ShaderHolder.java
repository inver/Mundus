package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.Renderable;

@Slf4j
@Getter
public class ShaderHolder implements Disposable, Cloneable {

    protected final BaseShader defaultInstance;
    private boolean initialized = false;

    public ShaderHolder(BaseShader instance) {
        this.defaultInstance = instance;
    }

    public void init() {
        init(null);
    }

    public void init(Renderable renderable) {
        if (!initialized) {
            log.debug("Compile shader");
            defaultInstance.init(renderable);
            initialized = true;
        }
    }

    @Override
    public ShaderHolder clone() {
        return new ShaderHolder(defaultInstance);
    }

    @Override
    public void dispose() {
        defaultInstance.dispose();
    }
}
