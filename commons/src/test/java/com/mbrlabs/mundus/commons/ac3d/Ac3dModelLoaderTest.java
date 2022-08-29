package com.mbrlabs.mundus.commons.ac3d;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
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
