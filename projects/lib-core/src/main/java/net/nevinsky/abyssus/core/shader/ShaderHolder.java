package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ShaderHolder implements Disposable {

    protected final BaseShader defaultInstance;
    private boolean initialized = false;

    public ShaderHolder(BaseShader instance) {
        this.defaultInstance = instance;
    }

    public void init() {
        if (!initialized) {
            log.debug("Compile shader");
            defaultInstance.init();
            initialized = true;
        }
    }

    @Override
    public void dispose() {
        defaultInstance.dispose();
    }
}
