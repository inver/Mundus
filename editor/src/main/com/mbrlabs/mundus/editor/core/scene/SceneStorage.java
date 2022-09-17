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

package com.mbrlabs.mundus.editor.core.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.dto.SceneDto;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.commons.skybox.Skybox;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class SceneStorage {

    private final ObjectMapper mapper;
    protected final AssetsStorage assetsStorage;

    public Scene createDefault(String projectPath, int id) {
        var scene = new Scene();
        scene.setName(DEFAULT_SCENE_NAME);
        scene.setSkybox(createDefaultSkybox());
        scene.getEnvironment().setFog(new Fog());
        scene.setId(id);

        var defCamera = createDefaultCamera();
        scene.getCameras().add(defCamera);
//        scene.setCurrentCamera(defCamera);

        scene.getEnvironment().add(createDefaultDirectionalLight());
        scene.getEnvironment().setAmbientLight(createDefaultAmbientLight());

        saveScene(projectPath, scene);

        return scene;
    }

    private Camera createDefaultCamera() {
        var cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 1, -3);
        cam.lookAt(0, 1, -1);
        cam.near = 0.2f;
        cam.far = 10000;
        return cam;
    }

    private DirectionalLight createDefaultDirectionalLight() {
        var dirLight = new DirectionalLight();
        dirLight.color.set(1, 1, 1, 1);
        dirLight.intensity = 1f;
        dirLight.direction.set(0, -1f, 0);
        dirLight.direction.nor();
        return dirLight;
    }

    private BaseLight createDefaultAmbientLight() {
        var res = new BaseLight();
        res.intensity = 0.3f;
        return res;
    }

    protected Skybox createDefaultSkybox() {
        var texture = assetsStorage.loadAssetFile("textures/skybox/default/skybox_default.png");
        return new Skybox(texture, texture, texture, texture, texture, texture);
    }

    /**
     * Saves a scene.
     *
     * @param context project context of the scene
     * @param scene   scene to save
     */
    public void saveScene(String projectPath, Scene scene) {
        String sceneDir = getScenePath(projectPath, scene.getName());

        try {
            SceneDto dto = SceneConverter.convert(scene);
            FileHandle saveFile = Gdx.files.absolute(sceneDir);
            saveFile.writeString(mapper.writeValueAsString(dto), false);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    /**
     * Loads a scene.
     * <p>
     * Does however not initialize ModelInstances, Terrains, ... -> ProjectManager
     *
     * @param context   project context of the scene
     * @param sceneName name of the scene to load
     * @return loaded scene
     */
    public SceneDto loadScene(String projectPath, String sceneName) throws FileNotFoundException {
        String sceneDir = getScenePath(projectPath, sceneName);
        Json json = new Json();
        return json.fromJson(SceneDto.class, new FileInputStream(sceneDir));
    }

    private static String getScenePath(String projectPath, String sceneName) {
        return FilenameUtils.concat(
                projectPath + "/" + PROJECT_SCENES_DIR,
                sceneName + "." + PROJECT_SCENE_EXTENSION
        );
    }
}
