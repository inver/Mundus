package com.mbrlabs.mundus.commons.loader.obj;

import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.loader.ModelLoader;
import com.mbrlabs.mundus.commons.loader.obj.material.ObjMaterialLoader;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import lombok.SneakyThrows;
import net.nevinsky.mundus.core.model.ModelData;

public class ObjModelLoader extends ModelLoader<ModelLoader.ModelParameters> implements AppModelLoader {

    private final ObjMaterialLoader materialLoader = new ObjMaterialLoader();
    private final ObjLoader loader = new ObjLoader(new AbsoluteFileHandleResolver(), materialLoader);

    public ObjModelLoader() {
        super(null);
    }

    @SneakyThrows
    @Override
    public ModelData loadModelData(FileHandle fileHandle, ModelLoader.ModelParameters parameters) {
//        return loader.loadModelData(fileHandle);
        return null;
    }

    @Override
    public ImportedModel importModel(FileHandle handle) {
//        var model = loadModel(handle);
//        return new ImportedModel(model, handle);
        return null;
    }
}
