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
import com.mbrlabs.mundus.commons.dto.SceneDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * The converter for scene.
 */
@RequiredArgsConstructor
public class SceneConverter {

    private final ObjectMapper mapper;

    /**
     * Converts {@link Scene} to {@link SceneDto}.
     */
    @SneakyThrows
    public SceneDto convert(Scene scene) {
        SceneDto dto = new SceneDto();

        dto.setId(scene.getId());
        dto.setName(scene.getName());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scene.getWorld().getSystem(WorldSerializationManager.class).save(baos, new SaveFileFormat(
                scene.getWorld().getAspectSubscriptionManager().get(Aspect.all()).getEntities()
        ));
        var obj = mapper.readValue(baos.toString(), Object.class);
        dto.setEcs(obj);

        // getEnvironment() stuff
        dto.setAmbientLightEnabled(scene.getEnvironment().isAmbientLightEnabled());
        dto.setAmbientLight(scene.getEnvironment().getAmbientLight());

        dto.setFogEnabled(scene.getEnvironment().isFogEnabled());
        dto.setFog(scene.getEnvironment().getFog());

        dto.setSkyboxEnabled(scene.getEnvironment().isSkyboxEnabled());
        dto.setSkyboxName(scene.getEnvironment().getSkyboxName());
        return dto;
    }

    @SneakyThrows
    public void fillScene(Scene scene, SceneDto dto) {
        scene.setId(dto.getId());
        scene.setName(dto.getName());

        if (dto.getEcs() != null) {
            var str = mapper.writeValueAsBytes(dto.getEcs());
            scene.getWorld().getSystem(WorldSerializationManager.class)
                    .load(new ByteArrayInputStream(str), SaveFileFormat.class);
        }

        // getEnvironment() stuff
        if (dto.getAmbientLight() != null) {
            scene.getEnvironment()
                    .setAmbientLight(dto.getAmbientLight().getColor(), dto.getAmbientLight().getIntensity());
        }
        if (dto.isAmbientLightEnabled()) {
            scene.getEnvironment().enableAmbientLight();
        } else {
            scene.getEnvironment().disableAmbientLight();
        }
        if (dto.getFog() != null) {
            scene.getEnvironment()
                    .setFog(dto.getFog().getColor(), dto.getFog().getDensity(), dto.getFog().getGradient());
        }
        if (dto.isFogEnabled()) {
            scene.getEnvironment().enableFog();
        } else {
            scene.getEnvironment().disableFog();
        }

        scene.getEnvironment().setSkyboxEnabled(dto.isSkyboxEnabled());
        scene.getEnvironment().setSkyboxName(dto.getSkyboxName());
    }
}
