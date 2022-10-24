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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.AbstractComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;

/**
 * @author Guilherme Nemeth
 * @version 07-07-2017
 */
public class DirectionalLightComponent extends AbstractComponent {

    protected DirectionalLight light;

    public DirectionalLightComponent(GameObject go, DirectionalLight light) {
        super(go);
        type = Type.LIGHT;

        this.light = light;
        //todo
//        go.sceneGraph.scene.environment.add(light);
    }

    public DirectionalLight getDirectionalLight() {
        return light;
    }


    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {

    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void remove() {
//        gameObject.sceneGraph.scene.environment.remove(light);
    }

    @Override
    public Component clone(GameObject go) {
        return new DirectionalLightComponent(go, light.copy());
    }
}
