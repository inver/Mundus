package com.mbrlabs.mundus.commons.ac3d;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dModelLoader;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dParser;
import org.junit.Assert;
import org.junit.Test;

public class Ac3dModelLoaderTest {

    private static final Ac3dModelLoader loader = new Ac3dModelLoader(new Ac3dParser());

    @Test
    public void testLoad() {
        var res = loader.loadModel(new TestFileHandle("ac3d/shadow.ac", Files.FileType.Classpath));
        Assert.assertNotNull(res);
    }

    private static class TestFileHandle extends FileHandle {
        public TestFileHandle(String fileName, Files.FileType type) {
            super(fileName, type);
        }
    }
}
