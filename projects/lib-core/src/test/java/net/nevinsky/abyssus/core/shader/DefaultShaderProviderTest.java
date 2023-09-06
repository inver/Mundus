package net.nevinsky.abyssus.core.shader;

import net.nevinsky.abyssus.core.Renderable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultShaderProviderTest {

    private final Shader shader = mock(Shader.class);

    private final ShaderProvider provider = new DefaultShaderProvider() {
        @Override
        protected Shader createShader(ShaderHolder holder, Renderable renderable) {
            return shader;
        }
    };

    private final Renderable renderable = mock(Renderable.class);


    @BeforeEach
    public void init() {
        when(shader.canRender(any())).thenReturn(true);
    }


    @Test
    public void testGetDefaultShader() {
        var shader = provider.get(renderable);
        var defShader = provider.get(ShaderProvider.DEFAULT_SHADER_KEY, renderable);
        Assertions.assertEquals(shader, defShader);
    }

    @Disabled
    @Test
    public void testGetUnknownShader() {
        var shader = provider.get("ololo", renderable);
        Assertions.assertNull(shader);
    }
}
