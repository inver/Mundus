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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @JsonIgnore
    private final String tmpDir;

    @Setter
    private ProjectRef lastProject;
    @Setter
    private Settings settings = new Settings();

    @JsonCreator
    public Registry(@JsonProperty("tmpDir") String tmpDir) {
        if (StringUtils.isEmpty(tmpDir)) {
            this.tmpDir = System.getProperty("java.io.tmpdir");
        } else {
            this.tmpDir = tmpDir;
        }
    }

    public FileHandle createTempFolder() {
        String tempFolderPath = FilenameUtils.concat(tmpDir, UUID.randomUUID().toString()) + "/";
        FileHandle tempFolder = Gdx.files.absolute(tempFolderPath);
        tempFolder.mkdirs();

        return tempFolder;
    }

    public void purgeTempDirectory() {
        for (FileHandle f : Gdx.files.absolute(tmpDir).list()) {
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
