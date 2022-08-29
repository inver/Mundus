package com.mbrlabs.mundus.commons.core;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;

public interface AppModelLoader {
    String getKey();

    Model loadModel(final FileHandle fileHandle);

    ModelFiles getFileWithDependencies(FileHandle handle);
}
