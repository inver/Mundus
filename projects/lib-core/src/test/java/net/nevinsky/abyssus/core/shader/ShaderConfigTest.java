package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShaderConfigTest {

    @Test
    public void testDefaultValues() {
        var config = new ShaderConfig();
        Assertions.assertEquals(GL20.GL_BACK, config.getDefaultCullFace());
        Assertions.assertEquals(GL20.GL_LEQUAL, config.getDefaultDepthFunc());
    }
}
