package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import net.nevinsky.abyssus.core.model.Model;

public interface ModelLoader {
    Model loadModel(FileHandle fileHandle);

    ImportedModel importModel(FileHandle fileHandle);
}
