package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import lombok.SneakyThrows;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.mesh.Mesh;
import net.nevinsky.abyssus.core.mesh.MeshPart;
import net.nevinsky.abyssus.core.shader.AbstractShaderProvider;
import net.nevinsky.abyssus.core.shader.Shader;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EditorShaderProviderTest {

    private static EditorShaderProvider provider;

    private static final ShaderClassLoader loader = mock(ShaderClassLoader.class);
    private static final ShaderAsset asset = mock(ShaderAsset.class);
    private static final ShaderAsset asset2 = mock(ShaderAsset.class);
    private static final Renderable renderable = new Renderable();

    @BeforeAll
    public static void init() {
        when(asset.getVertexShader()).thenReturn("ololo");
        when(asset.getFragmentShader()).thenReturn("alala");
        when(loader.createShaderClass(asset)).thenReturn(StubShader.class);
        when(loader.createShaderClass(asset2)).thenReturn(Shader.class);
        provider = new EditorShaderProvider(loader, key -> {
            if (ShaderProvider.DEFAULT_SHADER_KEY.equals(key)) {
                return asset;
            }
            if ("ololo".equals(key)) {
                return asset2;
            }
            return null;
        });

        var vertexAttributes = new VertexAttributes(
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
        var mesh = mock(Mesh.class);
        renderable.meshPart.mesh = mesh;
        when(mesh.getVertexAttributes()).thenReturn(vertexAttributes);
    }

    @Test
    public void testGetShader() {
        StubShader shader = (StubShader) provider.get(renderable);
        Assertions.assertEquals("ololo", shader.getVertex());
        Assertions.assertEquals("alala", shader.getFragment());
    }

    @Test
    public void testGetUnknownShader() {
        var shader = provider.get("alala", renderable);
        Assertions.assertNull(shader);
    }

    @Test
    public void testExceptionOnClassInstantiate() {
        var shader = provider.get("ololo", renderable);
        Assertions.assertNull(shader);
    }

    @Test
    @SneakyThrows
    public void testRemoveShader() {
        provider.get(renderable);

        Map cache = getShaderCacheField();
        var size = cache.size();

        provider.remove(ShaderProvider.DEFAULT_SHADER_KEY);
        Assertions.assertEquals(size - 1, cache.size());
    }

    @Test
    @SneakyThrows
    public void testClearShaders() {
        provider.get(renderable);

        Map cache = getShaderCacheField();
        Assertions.assertFalse(cache.isEmpty());

        provider.clear();
        Assertions.assertEquals(0, cache.size());
    }

    private static Map getShaderCacheField() throws NoSuchFieldException, IllegalAccessException {
        Field cacheField = AbstractShaderProvider.class.getDeclaredField("shaderCache");
        cacheField.setAccessible(true);
        Map cache = (Map) cacheField.get(provider);
        return cache;
    }
}
