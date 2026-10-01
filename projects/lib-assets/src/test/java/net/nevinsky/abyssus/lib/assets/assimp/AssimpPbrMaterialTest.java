package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import net.nevinsky.abyssus.core.model.PbrModelMaterial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssimpPbrMaterialTest {

    private static FileHandle resource(String path) {
        return new FileHandle(new File(Objects.requireNonNull(
                AssimpPbrMaterialTest.class.getResource(path)).getFile()));
    }

    @Test
    void pbrPropertiesAndTexturesAreRead(@TempDir Path tmp) {
        var data = new AssimpModelDataLoader().load("pbr", resource("/gltf/pbr.gltf"), AssimpFlags.DEFAULT,
                new FileHandle(tmp.toFile()));

        var material = assertInstanceOf(PbrModelMaterial.class, data.materials.first());
        assertEquals("pbr", material.id);
        assertEquals(0.25f, material.metallic, 1e-5f);
        assertEquals(0.75f, material.roughness, 1e-5f);
        assertEquals(0.5f, material.baseColor.r, 1e-5f);
        assertTrue(material.doubleSided);
        assertEquals(PbrModelMaterial.AlphaMode.MASK, material.alphaMode);
        assertEquals(0.3f, material.alphaCutoff, 1e-5f);

        var usages = new HashSet<Integer>();
        for (var texture : material.textures) {
            usages.add(texture.usage);
        }
        assertEquals(5, material.textures.size, "each usage exactly once: " + usages);
        assertTrue(usages.contains(ModelTexture.USAGE_DIFFUSE));
        assertTrue(usages.contains(ModelTexture.USAGE_NORMAL));
        assertTrue(usages.contains(ModelTexture.USAGE_EMISSIVE));
        assertTrue(usages.contains(PbrModelMaterial.USAGE_METALLIC_ROUGHNESS));
        assertTrue(usages.contains(PbrModelMaterial.USAGE_OCCLUSION));
    }

    @Test
    void nonPbrMaterialStaysPlain() {
        var data = new AssimpModelDataLoader().load("tri", resource("/obj/tri.obj"));
        assertNotNull(data.materials.first());
        assertFalse(data.materials.first() instanceof PbrModelMaterial);
    }
}
