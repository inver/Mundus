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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.editor.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.EditorScene;
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

    public ProjectSettings settings;
    public String path;
    public String name;

    public Array<String> scenes;
    public EditorScene currScene;

    public EditorAssetManager assetManager;

    private final AtomicInteger idProvider;

    /**
     * set by kryo when project is loaded. do not use this
     */
    public String activeSceneName;

    public ProjectContext(int idProvider) {
        this(new AtomicInteger(idProvider));
    }

    public ProjectContext(AtomicInteger idProvider) {
        scenes = new Array<>();
        settings = new ProjectSettings();
        currScene = new EditorScene();
        this.idProvider = idProvider;
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
        if (assetManager != null) {
            assetManager.dispose();
        }
    }

}
