package com.mbrlabs.mundus.commons.core;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.mbrlabs.mundus.commons.model.ModelFiles;

public interface AppModelLoader {

    Model loadModel(final FileHandle fileHandle);

    ModelFiles getFileWithDependencies(FileHandle handle);
}
