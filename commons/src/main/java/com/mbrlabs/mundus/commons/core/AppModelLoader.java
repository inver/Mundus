package com.mbrlabs.mundus.commons.core;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import net.nevinsky.abyssus.core.model.Model;

//todo remove this interface
public interface AppModelLoader {

    Model loadModel(final FileHandle fileHandle);

    ImportedModel importModel(FileHandle handle);
}
