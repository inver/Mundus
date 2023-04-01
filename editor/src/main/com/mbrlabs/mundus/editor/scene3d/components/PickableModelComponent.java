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

package com.mbrlabs.mundus.editor.scene3d.components;

import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import com.mbrlabs.mundus.editor.tools.picker.PickerColorEncoder;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.ModelInstance;

/**
 * @author Marcus Brummer
 * @version 27-10-2016
 */
public class PickableModelComponent extends ModelComponent implements PickableComponent {

    public PickableModelComponent(GameObject go, String shaderKey) {
        super(go, shaderKey);
    }

    @Override
    public void encodeRayPickColorId() {
        var goIDa = PickerColorEncoder.encodeRaypickColorId(gameObject);
        modelInstance.materials.first().set(goIDa);
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        batch.render(modelInstance, shaders.get(shaderKey));
    }

    @Override
    public Component clone(GameObject go) {
        PickableModelComponent mc = new PickableModelComponent(go, shaderKey);
        mc.modelAsset = this.modelAsset;
        mc.modelInstance = new ModelInstance(modelAsset.getModel());
        mc.encodeRayPickColorId();
        return mc;
    }
}
