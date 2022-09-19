/*
 * Copyright (c) 2021. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.importer;

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.dto.ModelComponentDto;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Converter for model component.
 */
@Slf4j
public class ModelComponentConverter {

    public static ModelComponent convert(ModelComponentDto dto, GameObject go, Map<String, Asset<?>> assets, Map<String, BaseShader> shaders) {
        var model = (ModelAsset) assets.get(dto.getModelID());

        if (model == null) {
            log.error("MModel for MModelInstance not found: {}", dto.getModelID());
            return null;
        }

        var component = new ModelComponent(go, shaders.get(dto.getShaderKey()));
        component.setModel(model, false);

        for (String g3dbMatID : dto.getMaterials().keySet()) {
            String uuid = dto.getMaterials().get(g3dbMatID);
            MaterialAsset matAsset = (MaterialAsset) assets.get(uuid);
            component.getMaterials().put(g3dbMatID, matAsset);
        }

        return component;
    }

    public static ModelComponentDto convert(ModelComponent modelComponent) {
        var dto = new ModelComponentDto();
        dto.setModelID(modelComponent.getModelAsset().getID());

        // materials
        for (var id : modelComponent.getMaterials().keys()) {
            dto.getMaterials().put(id, modelComponent.getMaterials().get(id).getID());
        }

        return dto;
    }
}
