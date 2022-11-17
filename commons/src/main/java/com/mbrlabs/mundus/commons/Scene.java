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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.components.Renderable;
import com.mbrlabs.mundus.commons.shaders.ShaderHolder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable, Renderable {

    private long id;
    private String name;

    @Setter
    private SceneGraph sceneGraph = new SceneGraph();

    @Getter
    @Setter
    private SceneEnvironment environment = new SceneEnvironment();
    @Getter
    private final List<Asset<?>> assets = new ArrayList<>();
    @Getter
    private final List<Camera> cameras = new ArrayList<>();
//    @Getter
//    private final List<BaseLight> lights = new ArrayList<>();
//    @Getter
//    private final List<MaterialAsset> materials = new ArrayList<>();
//    @Getter
//    private final List<TextureAsset> textures = new ArrayList<>();
//
//    @Deprecated // TODO not here
//    public Array<TerrainAsset> terrains;

    //    public PerspectiveCamera cam;
//    public ModelBatch batch;


    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderHolder shaders, float delta) {
        sceneGraph.render(batch, environment, shaders, delta);
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

    public SceneGraph getSceneGraph() {
        return sceneGraph;
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Scene scene = (Scene) o;

        return new EqualsBuilder().append(id, scene.id)
                .append(name, scene.name)
                .append(environment, scene.environment)
                .append(assets, scene.assets)
                .append(cameras, scene.cameras)
                .append(sceneGraph, scene.sceneGraph)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(sceneGraph)
                .append(environment)
                .append(assets)
                .append(cameras)
                .toHashCode();
    }
}
