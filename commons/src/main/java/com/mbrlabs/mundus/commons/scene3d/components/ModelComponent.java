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

package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.Material;
import net.nevinsky.mundus.core.ModelBatch;
import net.nevinsky.mundus.core.ModelInstance;
import com.badlogic.gdx.utils.ObjectMap;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 17-01-2016
 */
public class ModelComponent extends AbstractComponent implements AssetUsage {

    protected ModelAsset modelAsset;
    protected ModelInstance modelInstance;
    protected String shaderKey;

    protected ObjectMap<String, MaterialAsset> materials;  // g3db material id to material asset uuid

    public ModelComponent(GameObject go, String shaderKey) {
        super(go);
        type = Type.MODEL;
        materials = new ObjectMap<>();
        this.shaderKey = shaderKey;
    }

    public void setShaderKey(String shaderKey) {
        this.shaderKey = shaderKey;
    }

    public void setModel(ModelAsset model, boolean inheritMaterials) {
        this.modelAsset = model;
        modelInstance = new ModelInstance(model.getModel());
        modelInstance.transform = gameObject.getTransform();

        // apply default materials of model
        if (inheritMaterials) {
            for (String key : model.getDefaultMaterials().keySet()) {
                materials.put(key, model.getDefaultMaterials().get(key));
            }
        }
        applyMaterials();
    }

    public ObjectMap<String, MaterialAsset> getMaterials() {
        return materials;
    }

    public ModelAsset getModelAsset() {
        return modelAsset;
    }

    public void applyMaterials() {
        for (Material mat : modelInstance.materials) {
            MaterialAsset materialAsset = materials.get(mat.id);
            if (materialAsset == null) continue;

            materialAsset.applyToMaterial(mat);
        }
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        modelInstance.transform.set(gameObject.getTransform());
        batch.render(modelInstance, environment, shaders.get(shaderKey));
    }

    @Override
    public Component clone(GameObject go) {
        ModelComponent mc = new ModelComponent(go, shaderKey);
        mc.modelAsset = this.modelAsset;
        mc.modelInstance = new ModelInstance(modelAsset.getModel());
        return mc;
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (Objects.equals(assetToCheck.getID(), modelAsset.getID()))
            return true;

        if (assetToCheck instanceof MaterialAsset) {
            if (materials.containsValue(assetToCheck, true)) {
                return true;
            }
        }

        if (assetToCheck instanceof TextureAsset) {
            // for each texture see if there is a match
            for (ObjectMap.Entry<String, MaterialAsset> next : materials) {
                if (next.value.usesAsset(assetToCheck)) {
                    return true;
                }
            }
        }

        return false;
    }
}
