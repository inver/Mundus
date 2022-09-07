/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mbrlabs.mundus.commons.assets.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dModelLoader;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dParser;
import com.mbrlabs.mundus.commons.loader.g3d.MG3dModelLoader;
import com.mbrlabs.mundus.commons.loader.gltf.GltfLoaderWrapper;
import com.mbrlabs.mundus.commons.loader.obj.ObjLoaderWrapper;
import com.mbrlabs.mundus.commons.utils.FileFormatUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
public class ModelAsset extends Asset {

    private Model model;
    private final Map<String, MaterialAsset> defaultMaterials;

    public ModelAsset(Meta meta, FileHandle assetFile) {
        super(meta, assetFile);
        defaultMaterials = new HashMap<>();
    }

    public Model getModel() {
        return model;
    }

    public Map<String, MaterialAsset> getDefaultMaterials() {
        return defaultMaterials;
    }

    @Override
    public void load() {
        // TODO don't create a new loader each time
        if (FileFormatUtils.isG3DB(file)) {
            MG3dModelLoader loader = new MG3dModelLoader(new UBJsonReader());
            model = loader.loadModel(file);
        } else if (FileFormatUtils.isGLTF(file)) {
            GltfLoaderWrapper loader = new GltfLoaderWrapper(new Json());
            model = loader.loadModel(file);
        } else if (FileFormatUtils.isAC3D(file)) {
            Ac3dModelLoader loader = new Ac3dModelLoader(new Ac3dParser());
            model = loader.loadModel(file);
        } else if (FileFormatUtils.isOBJ(file)) {
            model = new ObjLoaderWrapper().loadModel(file);
        } else {
            throw new GdxRuntimeException("Unsupported 3D model");
        }
    }

    @Override
    public void resolveDependencies(Map<String, Asset> assets) {
        try {
            // materials
            var metaModel = meta.getModel();
            if (metaModel == null) {
                return;
            }

            for (String g3dbMatID : metaModel.getDefaultMaterials().keys()) {
                String uuid = metaModel.getDefaultMaterials().get(g3dbMatID);
                defaultMaterials.put(g3dbMatID, (MaterialAsset) assets.get(uuid));
            }
        } catch (Exception e) {
            //todo display error
            log.error("ERROR", e);
        }
    }

    @Override
    public void applyDependencies() {
        if (model == null) return;

        // materials
        for (Material mat : model.materials) {
            MaterialAsset materialAsset = defaultMaterials.get(mat.id);
            if (materialAsset == null) continue;
            materialAsset.applyToMaterial(mat);
        }
    }

    @Override
    public void dispose() {
        if (model != null) {
            model.dispose();
        }
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        // if it's a MaterialAsset compare to the models materials
        if (assetToCheck instanceof MaterialAsset) {
            return defaultMaterials.containsValue(assetToCheck);
        }

        // check if the materials use the asset, like a texture asset
        for (Map.Entry<String, MaterialAsset> stringMaterialAssetEntry : defaultMaterials.entrySet()) {
            if (stringMaterialAssetEntry.getValue().usesAsset(assetToCheck)) {
                return true;
            }
        }
        return false;
    }
}
