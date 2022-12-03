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

    private final ModelComponentConverter modelComponentConverter;
    private final CameraConverter cameraConverter;

    /**
     * Converts {@link GameObjectDto} to {@link GameObject}.
     */
    public GameObject convert(GameObjectDto dto, Map<String, Asset<?>> assets) {
        final GameObject go = new GameObject(dto.getName(), dto.getId());
        go.setActive(dto.isActive());

        // transformation
        final float[] transform = dto.getTransform();
        go.translate(transform[0], transform[1], transform[2]);
        go.rotate(transform[3], transform[4], transform[5], transform[6]);
        go.scale(transform[7], transform[8], transform[9]);

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
        if (dto.getModelComponent() != null) {
            go.getComponents().add(modelComponentConverter.convert(dto.getModelComponent(), go, assets));
        } else if (dto.getTerrainComponent() != null) {
            go.getComponents().add(TerrainComponentConverter.convert(dto.getTerrainComponent(), go, assets));
        }

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

        GameObjectDto descriptor = new GameObjectDto();
        descriptor.setName(go.name);
        descriptor.setId(go.getId());
        descriptor.setActive(go.isActive());

        // translation
        go.getLocalPosition(TEMP_VEC);
        final float[] transform = descriptor.getTransform();
        transform[0] = TEMP_VEC.x;
        transform[1] = TEMP_VEC.y;
        transform[2] = TEMP_VEC.z;

        // rotation
        go.getLocalRotation(TEMP_QUAT);
        transform[3] = TEMP_QUAT.x;
        transform[4] = TEMP_QUAT.y;
        transform[5] = TEMP_QUAT.z;
        transform[6] = TEMP_QUAT.w;

        // scaling
        go.getLocalScale(TEMP_VEC);
        transform[7] = TEMP_VEC.x;
        transform[8] = TEMP_VEC.y;
        transform[9] = TEMP_VEC.z;

        // convert components
        for (Component c : go.getComponents()) {
            if (c.getType() == Component.Type.MODEL) {
                descriptor.setModelComponent(modelComponentConverter.convert((ModelComponent) c));
            } else if (c.getType() == Component.Type.TERRAIN) {
                descriptor.setTerrainComponent(TerrainComponentConverter.convert((TerrainComponent) c));
            } else if (c.getType() == Component.Type.CAMERA) {
                descriptor.getComponents().add(cameraConverter.fromComponent((CameraComponent) c));
            }
        }

        // convert tags
        if (go.getTags() != null && !go.getTags().isEmpty()) {
            for (String tag : go.getTags()) {
                descriptor.getTags().add(tag);
            }
        }

        // recursively convert children
        if (go.getChildren() != null) {
            for (GameObject c : go.getChildren()) {
                descriptor.getChildren().add(convert(c));
            }
        }

        return descriptor;
    }
}
