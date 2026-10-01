package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.BaseTest;
import net.nevinsky.abyssus.lib.assets.assimp.AssimpModelDataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssimpModelLoaderTest extends BaseTest {

    private final AssimpModelLoader loader = new AssimpModelLoader();

    @Test
    public void testExportObjFileWithTextures(@TempDir Path tmp) {
        var output = new FileHandle(tmp.toFile());

        loader.loadModelAndSaveForAsset(getHandle("/obj/piper/piper_pa18.obj"), output.child("model.gltf"));

        assertTrue(output.child("model.gltf").exists());
        assertTrue(output.child("model.bin").exists());
        assertTrue(output.child("textures/piper_refl.jpg").exists());
        assertTrue(output.child("textures/piper_diffuse.jpg").exists());
        assertTrue(output.child("textures/piper_bump.jpg").exists());
    }

    @Test
    public void testLoadObjFileToModelData() {
        var data = new AssimpModelDataLoader().load("piper", getHandle("/obj/piper/piper_pa18.obj"));

        assertFalse(data.meshes.isEmpty());
        assertFalse(data.materials.isEmpty());
        assertEquals(1, data.nodes.size);
        for (var mesh : data.meshes) {
            assertTrue(mesh.vertices.length > 0);
            assertEquals(0, mesh.parts[0].indices.length % 3);
        }
    }

    @Test
    public void testLoadAc3dFileToModelData() {
        var data = new AssimpModelDataLoader().load("sr22", getHandle("/ac3d/sr22.ac"));

        assertFalse(data.meshes.isEmpty());
        assertEquals(1, data.nodes.size);
    }
}
