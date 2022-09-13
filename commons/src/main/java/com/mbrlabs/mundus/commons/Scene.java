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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.env.AppEnvironment;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Brummer
 * @version 22-12-2015
 */
public class Scene implements Disposable {

    private String name;
    private long id;

    public SceneGraph sceneGraph;
    public AppEnvironment environment;
    public Skybox skybox;

    @Getter
    private final List<MaterialAsset> materials = new ArrayList<>();
    @Getter
    private final List<TextureAsset> textures = new ArrayList<>();

    @Deprecated // TODO not here
    public Array<TerrainAsset> terrains;

    public PerspectiveCamera cam;
    public ModelBatch batch;

    public Scene() {
        environment = new AppEnvironment();
        terrains = new Array<>();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0, 1, -1);
        cam.near = 0.2f;
        cam.far = 10000;

        var dirLight = new DirectionalLight();
        dirLight.color.set(1, 1, 1, 1);
        dirLight.intensity = 1f;
        dirLight.direction.set(0, -1f, 0);
        dirLight.direction.nor();
        environment.add(dirLight);
        environment.getAmbientLight().intensity = 0.3f;

        sceneGraph = new SceneGraph(this);
    }

    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        batch.begin(cam);
        sceneGraph.render(delta);
        batch.end();
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

    public void addMaterial(MaterialAsset asset) {
        materials.add(asset);
    }

    public void addTexture(TextureAsset asset) {
        textures.add(asset);
    }

    @Override
    public void dispose() {
        if (skybox != null) {
            skybox.dispose();
        }
    }
}
