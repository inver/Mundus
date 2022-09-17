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

import com.badlogic.gdx.utils.Disposable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mbrlabs.mundus.commons.Scene;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A project context represents a loaded and opened project.
 * <p>
 * A project context can have many scenes, not only one scene at a time can be
 * active.
 *
 * @author Marcus Brummer
 * @version 28-11-2015
 */
@Slf4j
public class ProjectContext implements Disposable {
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

    public ProjectContext(@JsonProperty("idProvider") int startId) {
        settings = new ProjectSettings();
        currentScene = new Scene();
        idProvider = new AtomicInteger(startId);
    }

    public int obtainID() {
        return idProvider.incrementAndGet();
    }

    public int inspectCurrentID() {
        return idProvider.get();
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

    public int getIdProvider() {
        return inspectCurrentID();
    }

//    public Camera getCurrentCamera() {
//        return currentScene.getCurrentCamera();
//    }
}
