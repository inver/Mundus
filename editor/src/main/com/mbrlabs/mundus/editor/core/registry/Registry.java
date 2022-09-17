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

package com.mbrlabs.mundus.editor.core.registry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.TEMP_DIR;

/**
 * Manages global settings.
 * <p>
 * Files are stored in ~/.mundus/
 *
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class Registry {
    private final List<ProjectRef> projects = new ArrayList<>();

    @Setter
    private ProjectRef lastProject;
    @Setter
    private Settings settings = new Settings();

    public FileHandle createTempFolder() {
        String tempFolderPath = FilenameUtils.concat(TEMP_DIR, UUID.randomUUID().toString()) + "/";
        FileHandle tempFolder = Gdx.files.absolute(tempFolderPath);
        tempFolder.mkdirs();

        return tempFolder;
    }

    public void purgeTempDirectory() {
        for (FileHandle f : Gdx.files.absolute(TEMP_DIR).list()) {
            f.deleteDirectory();
        }
    }

    public ProjectRef createProjectRef(String name, String folder) {
        var projectRef = new ProjectRef();
        projectRef.setName(name);
        projectRef.setPath(FilenameUtils.concat(folder, name));

        projects.add(projectRef);

        return projectRef;
    }

    // for kotlin interop
    public List<ProjectRef> getProjects() {
        return projects;
    }

    public ProjectRef getLastProject() {
        return lastProject;
    }

    public Settings getSettings() {
        return settings;
    }
}