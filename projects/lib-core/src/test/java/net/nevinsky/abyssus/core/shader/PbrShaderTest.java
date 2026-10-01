package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.Color;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.nevinsky.abyssus.core.Renderable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PbrShaderTest {

    private static Renderable renderable(Material material) {
        var renderable = new Renderable();
        renderable.material = material;
        return renderable;
    }

    @Test
    void metallicOrRoughnessSelectsPbr() {
        assertTrue(PbrShader.isPbr(renderable(new Material(PBRFloatAttribute.createMetallic(0f)))));
        assertTrue(PbrShader.isPbr(renderable(new Material(PBRFloatAttribute.createRoughness(1f)))));
    }

    @Test
    void plainMaterialIsNotPbr() {
        assertFalse(PbrShader.isPbr(renderable(new Material(ColorAttribute.createDiffuse(Color.RED)))));
        assertFalse(PbrShader.isPbr(renderable(null)));
    }

    @Test
    void configCopyIsIndependent() {
        var config = new ShaderConfig("v", "f");
        config.setNumBones(33);
        var copy = config.copy();
        copy.setFragmentShader(null);

        assertNotSame(config, copy);
        assertEquals("f", config.getFragmentShader());
        assertEquals(33, copy.getNumBones());
    }

    @Test
    void bundledSourcesExist() {
        for (var resource : new String[]{ShaderSources.DEFAULT_VERTEX, ShaderSources.DEFAULT_FRAGMENT,
                ShaderSources.PBR_FRAGMENT}) {
            assertTrue(ShaderSources.read(resource).contains("void main"), resource);
        }
    }
}
