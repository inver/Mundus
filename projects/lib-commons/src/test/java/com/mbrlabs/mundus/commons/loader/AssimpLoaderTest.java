package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssimpLoaderTest extends BaseTest {

    private final AssimpWorker loader = new AssimpWorker();

    @Test
    public void testLoadOnlyObjFile() {
        var output = new FileHandle("/tmp/" + UUID.randomUUID() + "/");
        output.mkdirs();

        loader.loadModelAndSaveForAsset(getHandle("/obj/piper/piper_pa18.obj"), output.child("model.gltf"));

        var paths = FileUtils.listFiles(output.file(), null, true);
        assertTrue(paths.contains(output.child("textures/piper_refl.jpg").file()));
        assertTrue(paths.contains(output.child("textures/piper_diffuse.jpg").file()));
        assertTrue(paths.contains(output.child("textures/piper_bump.jpg").file()));
        assertTrue(paths.contains(output.child("model.gltf").file()));
        assertTrue(paths.contains(output.child("model.bin").file()));
    }

    @Test
    public void testLoadObjFileToModelData() {
        var model = loader.loadModelData(UUID.randomUUID().toString(), getHandle("/obj/piper/piper_pa18.obj"),
                AssimpWorker.FLAGS);
        assertNotNull(model);
    }

}
