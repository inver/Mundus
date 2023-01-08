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

import com.artemis.Aspect;
import com.artemis.io.SaveFileFormat;
import com.artemis.managers.WorldSerializationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.SceneDto;
import com.mbrlabs.mundus.commons.env.lights.AmbientLight;
import com.mbrlabs.mundus.commons.mapper.BaseLightConverter;
import com.mbrlabs.mundus.commons.mapper.FogConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * The converter for scene.
 */
@RequiredArgsConstructor
public class SceneConverter {

    private final ObjectMapper mapper;
    private final CameraConverter cameraConverter;

    /**
     * Converts {@link Scene} to {@link SceneDto}.
     */
    @SneakyThrows
    public SceneDto convert(Scene scene) {
        SceneDto dto = new SceneDto();

        dto.setId(scene.getId());
        dto.setName(scene.getName());
        dto.setSkyboxName(scene.getEnvironment().getSkyboxName());

        dto.setRootNode(scene.getRootNode());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scene.getWorld().getSystem(WorldSerializationManager.class).save(baos, new SaveFileFormat(
                scene.getWorld().getAspectSubscriptionManager().get(Aspect.all()).getEntities()
        ));
        var obj = mapper.readValue(baos.toString(), Object.class);
        dto.setEcs(obj);

        // getEnvironment() stuff
        dto.setFog(FogConverter.convert(scene.getEnvironment().getFog()));
        dto.setAmbientLight(BaseLightConverter.convert(scene.getEnvironment().getAmbientLight()));

        var cm = scene.getCameras().get(0);
        dto.setCamera(cameraConverter.fromCamera(cm));
        return dto;
    }

    @SneakyThrows
    public void fillScene(Scene scene, SceneDto dto, Map<String, Asset<?>> assets) {
        scene.setId(dto.getId());
        scene.setName(dto.getName());

        if (dto.getEcs() != null) {
            var str = mapper.writeValueAsBytes(dto.getEcs());
            scene.getWorld().getSystem(WorldSerializationManager.class)
                    .load(new ByteArrayInputStream(str), SaveFileFormat.class);
        }

        if (dto.getRootNode() != null) {
            scene.setRootNode(dto.getRootNode());
        }
        // getEnvironment() stuff
        scene.getEnvironment().setFog(FogConverter.convert(dto.getFog()));
        scene.getEnvironment().setSkyboxName(dto.getSkyboxName());
        AmbientLight ambientLight = BaseLightConverter.convert(dto.getAmbientLight());
        if (ambientLight != null) {
            scene.getEnvironment().setAmbientLight(ambientLight);
        }

        var camera = cameraConverter.fromDto(dto.getCamera());
        camera.update();
        scene.getCameras().add(camera);
    }
}
