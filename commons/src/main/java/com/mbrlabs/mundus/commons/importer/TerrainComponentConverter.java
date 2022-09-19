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
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.dto.TerrainComponentDto;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * The converter for terrain.
 */
@Slf4j
public class TerrainComponentConverter {

    private final static String TAG = TerrainComponentConverter.class.getSimpleName();

    public static TerrainComponent convert(TerrainComponentDto dto, GameObject go,
                                           Map<String, Asset<?>> assets, Map<String, BaseShader> shaders) {
        // find terrainAsset
        TerrainAsset terrain = (TerrainAsset) assets.get(dto.getTerrainID());

        if (terrain == null) {
            log.error("Terrain for TerrainInstance not found");
            return null;
        }

        terrain.getTerrain().transform = go.getTransform();
        var terrainComponent = new TerrainComponent(go, shaders.get(dto.getShaderKey()));
        terrainComponent.setTerrain(terrain);

        return terrainComponent;
    }

    public static TerrainComponentDto convert(TerrainComponent terrainComponent) {
        TerrainComponentDto descriptor = new TerrainComponentDto();
        descriptor.setTerrainID(terrainComponent.getTerrain().getID());

        return descriptor;
    }

}
