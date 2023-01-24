package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

public class AppFileHandle extends FileHandle {
    public AppFileHandle(String fileName, Files.FileType type) {
        super(fileName, type);
    }
}
