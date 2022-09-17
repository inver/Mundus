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
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable {

    private long id;
    private String name;

    @Setter
    private SceneGraph sceneGraph = new SceneGraph();

    @Getter
    private final SceneEnvironment environment = new SceneEnvironment();
    //todo move skybox to environment
    @Setter
    private Skybox skybox;
    @Getter
    private final List<Asset> assets = new ArrayList<>();
    @Getter
    private final List<Camera> cameras = new ArrayList<>();
    @Getter
    private final List<BaseShader> shaders = new ArrayList<>();
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

    public void render(ModelBatch batch, float delta) {
        sceneGraph.render(batch, environment, delta);
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

    public Skybox getSkybox() {
        return skybox;
    }

    @Override
    public void dispose() {
        if (skybox != null) {
            skybox.dispose();
        }
    }
}
