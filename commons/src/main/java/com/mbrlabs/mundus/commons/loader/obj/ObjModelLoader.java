package com.mbrlabs.mundus.commons.loader.obj;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.model.ModelFiles;
import lombok.SneakyThrows;

public class ObjModelLoader extends ModelLoader<ModelLoader.ModelParameters> implements AppModelLoader {

    private final ObjToLibGdxConverter converter = new ObjToLibGdxConverter();

    public ObjModelLoader() {
        super(null);
    }

    @SneakyThrows
    @Override
    public ModelData loadModelData(FileHandle fileHandle, ModelLoader.ModelParameters parameters) {
        var parser = new ObjParser(fileHandle.file());
        var obj = parser.parse();

        return converter.convert(obj);
    }

    @Override
    public ModelFiles getFileWithDependencies(FileHandle handle) {
        var model = loadModel(handle);
        return new ModelFiles(handle);
    }
}
