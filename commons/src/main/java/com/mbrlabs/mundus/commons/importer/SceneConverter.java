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

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.GameObjectDto;
import com.mbrlabs.mundus.commons.dto.SceneDto;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.mapper.BaseLightConverter;
import com.mbrlabs.mundus.commons.mapper.FogConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;

import java.util.Map;

/**
 * The converter for scene.
 */
public class SceneConverter {

    /**
     * Converts {@link Scene} to {@link SceneDto}.
     */
    public static SceneDto convert(Scene scene) {
        SceneDto dto = new SceneDto();

        dto.setId(scene.getId());
        dto.setName(scene.getName());

        // scene graph
        for (GameObject go : scene.getSceneGraph().getGameObjects()) {
            dto.getGameObjects().add(GameObjectConverter.convert(go));
        }

        // getEnvironment() stuff
        dto.setFog(FogConverter.convert(scene.getEnvironment().getFog()));
        dto.setAmbientLight(BaseLightConverter.convert(scene.getEnvironment().getAmbientLight()));

        var cm = scene.getCameras().get(0);
        // camera
        dto.setCamPosX(cm.position.x);
        dto.setCamPosY(cm.position.y);
        dto.setCamPosZ(cm.position.z);
        dto.setCamDirX(cm.direction.x);
        dto.setCamDirY(cm.direction.y);
        dto.setCamDirZ(cm.direction.z);
        return dto;
    }

    public static void fillScene(Scene scene, SceneDto dto, Map<String, Asset<?>> assets) {
        scene.setId(dto.getId());
        scene.setName(dto.getName());

        // getEnvironment() stuff
        scene.getEnvironment().setFog(FogConverter.convert(dto.getFog()));
        BaseLight ambientLight = BaseLightConverter.convert(dto.getAmbientLight());
        if (ambientLight != null) {
            scene.getEnvironment().setAmbientLight(ambientLight);
        }

        // scene graph
        scene.setSceneGraph(new SceneGraph());
        for (GameObjectDto descriptor : dto.getGameObjects()) {
            scene.getSceneGraph().addGameObject(GameObjectConverter.convert(descriptor, assets));
        }

        var camera = new PerspectiveCamera();
        // camera
        camera.position.x = dto.getCamPosX();
        camera.position.y = dto.getCamPosY();
        camera.position.z = dto.getCamPosZ();
        camera.direction.set(dto.getCamDirX(), dto.getCamDirY(), dto.getCamDirZ());
        camera.update();
        scene.getCameras().add(camera);
    }
}
