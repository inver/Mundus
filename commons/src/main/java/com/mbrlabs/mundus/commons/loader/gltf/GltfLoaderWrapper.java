package com.mbrlabs.mundus.commons.loader.gltf;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.loader.ModelLoader;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.nevinsky.mundus.core.model.Model;
import net.nevinsky.mundus.core.model.ModelData;
import org.apache.commons.lang3.NotImplementedException;

public class GltfLoaderWrapper extends ModelLoader<ModelLoader.ModelParameters> implements AppModelLoader {
//    private final GLTFLoader loader = new GLTFLoader();
    private final Json json;

    public GltfLoaderWrapper(Json json) {
        super(null);
        this.json = json;
    }

    @Override
    public ImportedModel importModel(FileHandle file) {
//        var model = loader.load(file);
//        var res = new ImportedModel(model.scene.model, file);
//
//        var dto = json.fromJson(GLTF.class, file);
//        dto.images.forEach(i -> {
//            var depFile = new FileHandle(file.parent().path() + '/' + i.uri);
//            if (depFile.exists()) {
//                res.getDependencies().add(depFile);
//            }
//        });
//        dto.buffers.forEach(b -> {
//            var depFile = new FileHandle(file.parent().path() + '/' + b.uri);
//            if (depFile.exists()) {
//                res.getDependencies().add(depFile);
//            }
//        });

//        return res;
        return null;
    }

    @Override
    public Model loadModel(FileHandle fileHandle) {
        return null;
//        return loader.load(fileHandle).scene.model;
    }

    @Override
    public ModelData loadModelData(FileHandle fileHandle, ModelParameters parameters) {
        throw new NotImplementedException();
    }
}
