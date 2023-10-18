package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;

public interface ModelLoader {
    void loadModelAndSaveForAsset(FileHandle from, FileHandle to);
}
