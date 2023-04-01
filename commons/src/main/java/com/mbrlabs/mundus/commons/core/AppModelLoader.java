package com.mbrlabs.mundus.commons.core;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.mbrlabs.mundus.commons.model.ImportedModel;

//todo remove this interface
public interface AppModelLoader {

    Model loadModel(final FileHandle fileHandle);

    ImportedModel importModel(FileHandle handle);
}
