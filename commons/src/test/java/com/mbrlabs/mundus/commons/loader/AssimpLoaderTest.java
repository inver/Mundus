package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.BaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class AssimpLoaderTest extends BaseTest {

    private final AssimpWorker loader = new AssimpWorker();

    @Test
    public void testLoadOnlyObjFile() {
        var output = new FileHandle("/tmp/" + UUID.randomUUID() + "/");
        output.mkdirs();

        loader.loadModelAndSaveForAsset(getHandle("/obj/piper/piper_pa18.obj"), output.child("model.gltf"));

        var paths = FileUtils.listFiles(output.file(), null, true);
        Assert.assertTrue(paths.contains(output.child("textures/piper_refl.jpg").file()));
        Assert.assertTrue(paths.contains(output.child("textures/piper_diffuse.jpg").file()));
        Assert.assertTrue(paths.contains(output.child("textures/piper_bump.jpg").file()));
        Assert.assertTrue(paths.contains(output.child("model.gltf").file()));
        Assert.assertTrue(paths.contains(output.child("model.bin").file()));
    }

}
