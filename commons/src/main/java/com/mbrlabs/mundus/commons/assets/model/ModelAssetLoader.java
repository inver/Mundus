package com.mbrlabs.mundus.commons.assets.model;

import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModelAssetLoader implements AssetLoader<ModelAsset, ModelMeta> {

    private final ModelImporter modelImporter;

    @Override
    public ModelAsset load(Meta<ModelMeta> meta) {
        var asset = new ModelAsset(meta);
        asset.load();
        return asset;

        //        if (FileFormatUtils.isG3DB(file)) {
//            MG3dModelLoader loader = new MG3dModelLoader(new UBJsonReader());
//            model = loader.loadModel(file);
//        } else if (FileFormatUtils.isGLTF(file)) {
//            GltfLoaderWrapper loader = new GltfLoaderWrapper(new Json());
//            model = loader.loadModel(file);
//        } else if (FileFormatUtils.isAC3D(file)) {
//            Ac3dModelLoader loader = new Ac3dModelLoader(new Ac3dParser());
//            model = loader.loadModel(file);
//        } else if (FileFormatUtils.isOBJ(file)) {
//            model = new ObjModelLoader().loadModel(file);
//        } else {
//            throw new GdxRuntimeException("Unsupported 3D model");
//        }
    }

}
