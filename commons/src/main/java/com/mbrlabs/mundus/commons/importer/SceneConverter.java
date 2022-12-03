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

import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.GameObjectDto;
import com.mbrlabs.mundus.commons.dto.SceneDto;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.mapper.BaseLightConverter;
import com.mbrlabs.mundus.commons.mapper.FogConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * The converter for scene.
 */
@RequiredArgsConstructor
public class SceneConverter {

    private final GameObjectConverter gameObjectConverter;
    private final CameraConverter cameraConverter;

    /**
     * Converts {@link Scene} to {@link SceneDto}.
     */
    public SceneDto convert(Scene scene) {
        SceneDto dto = new SceneDto();

        dto.setId(scene.getId());
        dto.setName(scene.getName());
        dto.setSkyboxName(scene.getEnvironment().getSkyboxName());

        // scene graph
        for (GameObject go : scene.getSceneGraph().getGameObjects()) {
            dto.getGameObjects().add(gameObjectConverter.convert(go));
        }

        // getEnvironment() stuff
        dto.setFog(FogConverter.convert(scene.getEnvironment().getFog()));
        dto.setAmbientLight(BaseLightConverter.convert(scene.getEnvironment().getAmbientLight()));

        var cm = scene.getCameras().get(0);
        dto.setCamera(cameraConverter.fromCamera(cm));
        return dto;
    }

    public void fillScene(Scene scene, SceneDto dto, Map<String, Asset<?>> assets) {
        scene.setId(dto.getId());
        scene.setName(dto.getName());

        // getEnvironment() stuff
        scene.getEnvironment().setFog(FogConverter.convert(dto.getFog()));
        scene.getEnvironment().setSkyboxName(dto.getSkyboxName());
        BaseLight ambientLight = BaseLightConverter.convert(dto.getAmbientLight());
        if (ambientLight != null) {
            scene.getEnvironment().setAmbientLight(ambientLight);
        }

        // scene graph
        scene.setSceneGraph(new SceneGraph());
        for (GameObjectDto descriptor : dto.getGameObjects()) {
            scene.getSceneGraph().addGameObject(gameObjectConverter.convert(descriptor, assets));
        }

        var camera = cameraConverter.fromDto(dto.getCamera());
        camera.update();
        scene.getCameras().add(camera);
    }
}
