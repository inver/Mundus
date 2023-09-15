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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.RenderableProvider;
import net.nevinsky.abyssus.core.model.Model;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
@Slf4j
public class ModelAsset extends Asset<ModelMeta> implements RenderableProvider {

    private Model model;
    private final Map<String, MaterialAsset> defaultMaterials = new HashMap<>();

    public ModelAsset(Meta<ModelMeta> meta) {
        super(meta);
    }

    public Model getModel() {
        return model;
    }

    public Map<String, MaterialAsset> getDefaultMaterials() {
        return defaultMaterials;
    }

    @Override
    public void dispose() {
        if (model != null) {
            model.dispose();
        }
    }

//    @Override
//    public boolean usesAsset(Asset assetToCheck) {
//        // if it's a MaterialAsset compare to the models materials
//        if (assetToCheck instanceof MaterialAsset) {
//            return defaultMaterials.containsValue(assetToCheck);
//        }
//
//        // check if the materials use the asset, like a texture asset
//        for (Map.Entry<String, MaterialAsset> stringMaterialAssetEntry : defaultMaterials.entrySet()) {
//            if (stringMaterialAssetEntry.getValue().usesAsset(assetToCheck)) {
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        throw new NotImplementedException();
    }
}
