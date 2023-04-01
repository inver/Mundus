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

import com.badlogic.gdx.math.Vector2;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import net.nevinsky.abyssus.core.ModelBatch;

import java.util.Objects;

/**
 * @author Marcus Brummer
 * @version 18-01-2016
 */
public class TerrainComponent extends AbstractComponent implements AssetUsage {

    protected TerrainAsset terrain;
    protected String shaderKey;

    public TerrainComponent(GameObject go, String shaderKey) {
        super(go);
        this.shaderKey = shaderKey;
        type = Component.Type.TERRAIN;
    }

    public void updateUVs(Vector2 uvScale) {
        terrain.updateUvScale(uvScale);
    }

    public void setTerrain(TerrainAsset terrain) {
        this.terrain = terrain;
    }

    public TerrainAsset getTerrain() {
        return terrain;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {

        //todo
//        batch.render(terrain.getTerrain(), environment, shaders.get(shaderKey));
    }

    @Override
    public Component clone(GameObject go) {
        // Cant be cloned right now
        return null;
    }

    @Override
    public boolean usesAsset(Asset assetToCheck) {
        if (Objects.equals(terrain.getID(), assetToCheck.getID()))
            return true;

        return terrain.usesAsset(assetToCheck);
    }
}
