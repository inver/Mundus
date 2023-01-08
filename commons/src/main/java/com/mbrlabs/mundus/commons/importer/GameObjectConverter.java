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

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.CameraDto;
import com.mbrlabs.mundus.commons.dto.GameObjectDto;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;
import com.mbrlabs.mundus.commons.dto.Vector4Dto;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.components.CameraComponent;
import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import com.mbrlabs.mundus.commons.scene3d.components.TerrainComponent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

/**
 * The converter for game object.
 */
@RequiredArgsConstructor
public class GameObjectConverter {

    private static final Vector3 TEMP_VEC = new Vector3();
    private static final Quaternion TEMP_QUAT = new Quaternion();

    private final CameraConverter cameraConverter;

    /**
     * Converts {@link GameObjectDto} to {@link GameObject}.
     */
    public GameObject convert(GameObjectDto dto, Map<String, Asset<?>> assets) {
        final GameObject go = new GameObject(dto.getName(), dto.getId());
        go.setActive(dto.isActive());

        // transformation
        go.translate(dto.getTransform().toVector());
        go.rotate(dto.getRotation().toQuaternion());
        go.scale(dto.getLocaleScale().toVector());

        // convert tags
        if (dto.getTags() != null || !dto.getTags().isEmpty()) {
            for (String tag : dto.getTags()) {
                go.addTag(tag);
            }
        }

        if (CollectionUtils.isNotEmpty(dto.getComponents())) {
            for (var component : dto.getComponents()) {
                if (component instanceof CameraDto) {
                    cameraConverter.addComponents(go, (CameraDto) component);
                }
            }
        }
        // convert components
//        if (dto.getModelComponent() != null) {
//            go.getComponents().add(modelComponentConverter.convert(dto.getModelComponent(), go, assets));
//        } else if (dto.getTerrainComponent() != null) {
//            go.getComponents().add(terrainComponentConverter.convert(dto.getTerrainComponent(), go, assets));
//        }

        // recursively convert children
        if (dto.getChildren() != null) {
            for (GameObjectDto c : dto.getChildren()) {
                go.addChild(convert(c, assets));
            }
        }

        return go;
    }

    /**
     * Converts {@link GameObject} to {@link GameObjectDto}.
     */
    public GameObjectDto convert(GameObject go) {

        var dto = new GameObjectDto();
        dto.setName(go.name);
        dto.setId(go.getId());
        dto.setActive(go.isActive());

        go.getLocalPosition(TEMP_VEC);
        dto.setTransform(new Vector3Dto(TEMP_VEC));

        go.getLocalRotation(TEMP_QUAT);
        dto.setRotation(new Vector4Dto(TEMP_QUAT));

        go.getLocalScale(TEMP_VEC);
        dto.setLocaleScale(new Vector3Dto(TEMP_VEC));

        // convert components
        for (Component c : go.getComponents()) {
            if (c.getType() == Component.Type.MODEL) {
//                dto.setModelComponent(modelComponentConverter.convert((ModelComponent) c));
            } else if (c.getType() == Component.Type.TERRAIN) {
//                dto.setTerrainComponent(terrainComponentConverter.convert((TerrainComponent) c));
            } else if (c.getType() == Component.Type.CAMERA) {
                dto.getComponents().add(cameraConverter.fromComponent((CameraComponent) c));
            }
        }

        // convert tags
        if (go.getTags() != null && !go.getTags().isEmpty()) {
            for (String tag : go.getTags()) {
                dto.getTags().add(tag);
            }
        }

        // recursively convert children
        if (go.getChildren() != null) {
            for (GameObject c : go.getChildren()) {
                dto.getChildren().add(convert(c));
            }
        }

        return dto;
    }
}
