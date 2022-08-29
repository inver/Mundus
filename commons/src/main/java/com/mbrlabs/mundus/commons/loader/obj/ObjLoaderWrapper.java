package com.mbrlabs.mundus.commons.loader.obj;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.core.ModelFiles;

public class ObjLoaderWrapper implements AppModelLoader {

    private final ObjLoader loader = new ObjLoader();

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public Model loadModel(FileHandle fileHandle) {
        return loader.loadModel(fileHandle);
    }

    @Override
    public ModelFiles getFileWithDependencies(FileHandle handle) {
        return new ModelFiles(handle);
    }
}
