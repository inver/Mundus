package com.mbrlabs.mundus.commons.core;

import com.badlogic.gdx.files.FileHandle;
import net.nevinsky.mundus.core.model.Model;
import com.mbrlabs.mundus.commons.model.ImportedModel;

public interface AppModelLoader {

    Model loadModel(final FileHandle fileHandle);

    ImportedModel importModel(FileHandle handle);
}
