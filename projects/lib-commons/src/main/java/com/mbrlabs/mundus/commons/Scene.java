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

package com.mbrlabs.mundus.commons;

import com.artemis.World;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.core.ecs.WorldUtils;
import com.mbrlabs.mundus.commons.core.ecs.behavior.RenderComponentSystem;
import com.mbrlabs.mundus.commons.core.ecs.component.CameraComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.components.RenderableObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
@Slf4j
@RequiredArgsConstructor
public class Scene implements Disposable, RenderableObject {
    private long id = 0;
    private String name;

    @Getter
    private final World world;
    @Getter
    @Setter
    private SceneEnvironment environment = new SceneEnvironment();

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        world.setDelta(delta);
        world.getSystem(RenderComponentSystem.class).setRenderData(batch, environment, shaders);
        world.process();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void dispose() {
    }

    public List<Pair<Camera, Integer>> getCameras() {
        return WorldUtils.getFromWorld(
                world, CameraComponent.class, (entityId, component) -> Pair.of(component.getCamera(), entityId)
        );
    }

    public Camera getCamera(int cameraId) {
        return world.getMapper(CameraComponent.class).get(cameraId).getCamera();
    }

    public void setAmbientLight(Color color, float intensity) {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        // todo implement intensity of ambient light in default shader
        log.warn("Intensity for ambient light doesn't support. Implement it");
    }

    public void removeAmbientLight() {
        environment.remove(ColorAttribute.AmbientLight);
    }
}
