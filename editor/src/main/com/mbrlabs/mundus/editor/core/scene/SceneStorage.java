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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.SceneDto;
import com.mbrlabs.mundus.commons.env.Fog;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.commons.utils.FileUtils;
import com.mbrlabs.mundus.editor.core.assets.AssetsStorage;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class SceneStorage {

    private final ObjectMapper mapper;
    protected final AssetsStorage assetsStorage;
    protected final EditorAssetManager editorAssetManager;

    public Scene createDefault(String projectPath, int id) {
        var scene = new Scene();
        scene.setName(DEFAULT_SCENE_NAME);
        scene.getEnvironment().setSkyboxName(DEFAULT_SKYBOX_NAME);
        scene.getEnvironment().setFog(new Fog());
        scene.setId(id);

        var defCamera = createDefaultCamera();
        scene.getCameras().add(defCamera);
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

    /**
     * Saves a scene.
     *
     * @param projectPath project path
     * @param scene       scene to save
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
     * @param projectPath project path
     * @param sceneName   name of the scene to load
     * @return loaded scene
     */
    @SneakyThrows
    public SceneDto loadScene(String projectPath, String sceneName) {
        var sceneDir = getScenePath(projectPath, sceneName);
        return mapper.readValue(new FileInputStream(sceneDir), SceneDto.class);
    }

    private static String getScenePath(String projectPath, String sceneName) {
        return FilenameUtils.concat(projectPath + "/" + PROJECT_SCENES_DIR, sceneName + "." + PROJECT_SCENE_EXTENSION);
    }

    @SneakyThrows
    public void copyAssetToProject(String projectPath, Asset<?> asset) {
        if (asset.getMeta().getFile().type() == Files.FileType.Classpath) {
            for (var path : FileUtils.getResourceFiles(asset.getMeta().getFile().path())) {
                var file = new File(getClass().getClassLoader()
                        .getResource(asset.getMeta().getFile().path() + "/" + path).toURI());
                org.apache.commons.io.FileUtils.copyFile(file, Paths.get(
                        projectPath + "/" + PROJECT_ASSETS_DIR + "/" + asset.getName() + "/" + path
                ).toFile());
            }
        } else {
            asset.getMeta().getFile()
                    .copyTo(new FileHandle(Paths.get(projectPath + "/" + PROJECT_ASSETS_DIR).toFile()));
        }
    }
}
