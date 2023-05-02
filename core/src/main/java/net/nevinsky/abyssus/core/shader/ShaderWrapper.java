package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ShaderWrapper implements Disposable {

    protected final BaseShader instance;
    private boolean initialized = false;

    public ShaderWrapper(BaseShader instance) {
        this.instance = instance;
    }

    public void init() {
        if (!initialized) {
            log.debug("Compile shader");
            instance.init();
            initialized = true;
        }
    }

    @Override
    public void dispose() {
        instance.dispose();
    }
}
