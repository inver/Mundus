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

package com.mbrlabs.mundus.editor.core.project;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Disposable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObject;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A project context represents a loaded and opened project.
 * <p>
 * A project context can have many scenes, not only one scene at a time can be active.
 *
 * @author Marcus Brummer
 * @version 28-11-2015
 */
@Slf4j
public class ProjectContext implements Disposable {

    public static final int MAIN_CAMERA_SELECTED = -1;
    private final AtomicInteger idProvider;

    public ProjectSettings settings;
    public String path;
    public String name;

//    @JsonIgnore
//    private final List<String> scenes = new ArrayList<>();

    @JsonIgnore
    private Scene currentScene;
    /**
     * set by jackson when project is deserialized from json file
     */
    @Getter
    private String activeSceneName;
    @Getter
    private final PerspectiveCamera mainCamera;
    private int selectedCamera = MAIN_CAMERA_SELECTED;

    @JsonCreator
    public ProjectContext(@JsonProperty("idProvider") int startId,
                          @JsonProperty("mainCamera") PerspectiveCamera camera) {
        settings = new ProjectSettings();
//        currentScene = new Scene(world);
        idProvider = new AtomicInteger(startId);
        mainCamera = camera;
    }

    public int obtainID() {
        return idProvider.incrementAndGet();
    }

    @JsonIgnore
    public Camera getCamera() {
        if (selectedCamera < 0) {
            return mainCamera;
        }

        if (currentScene == null || CollectionUtils.isEmpty(currentScene.getCameras())) {
            log.warn("Selected camera doesn't exist");
            return mainCamera;
        }
        return currentScene.getCamera(selectedCamera);
    }

    @Override
    public void dispose() {
        log.debug("Disposing current project: {}", path);
//        if (assetManager != null) {
//            assetManager.dispose();
//        }
    }

//    public List<String> getScenes() {
//        return scenes;
//    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
        if (currentScene != null) {
            activeSceneName = currentScene.getName();
        }
    }

    public <T extends RenderableObject> T getRenderableObject(Class<T> clazz, int entityId) {
        var delegate = currentScene.getWorld().getMapper(RenderComponent.class).get(entityId).getRenderable();
        if (!(delegate instanceof RenderableObjectDelegate)) {
            return null;
        }
        return (T) ((RenderableObjectDelegate) delegate).getAsset();
    }

    public int getSelectedCamera() {
        return selectedCamera;
    }

    public void setSelectedCamera(int selectedCamera) {
        this.selectedCamera = selectedCamera;
    }
}
