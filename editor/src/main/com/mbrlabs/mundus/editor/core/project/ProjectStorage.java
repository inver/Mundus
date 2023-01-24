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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.editor.config.AppEnvironment;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.core.registry.Registry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.PROJECT_EXTENSION;

/**
 * Manages descriptor object <-> file io.
 * <p>
 * This provides only method for loading the serialized data into POJOs. It does
 * not load or initialize any data (like for example it does not load meshes or
 * textures). This has to be done separately (ProjectManager).
 *
 * @author Marcus Brummer
 * @version 12-12-2015
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectStorage {

    private final ObjectMapper mapper;
    private final AppEnvironment appEnvironment;

    /**
     * Loads the registry.
     * <p>
     * Save to use afterwards, nothing else needs to be loaded.
     *
     * @return mundus registry
     */
    public Registry loadRegistry() {
        try (var fis = new FileInputStream(appEnvironment.getHomeDataFile())) {
            return mapper.readValue(fis, Registry.class);
        } catch (Exception e) {
            log.error("ERROR", e);
        }

        return new Registry(appEnvironment.getTempDir());
    }

    /**
     * Saves the registry
     *
     * @param registry mundus registry
     */
    public void saveRegistry(Registry registry) {
        try (var fos = new FileOutputStream(appEnvironment.getHomeDataFile())) {
            mapper.writeValue(fos, registry);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    /**
     * Saves the project context.
     * <p>
     * Saves only the project's .pro file, not the individual scenes.
     *
     * @param context project context to save
     */
    public void saveProjectContext(ProjectContext context) {
        try (var fos = new FileOutputStream(context.path + "/" + context.name + "." + PROJECT_EXTENSION)) {
            var res = mapper.writeValueAsString(context);
            IOUtils.write(res, fos, Charset.defaultCharset());
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    /**
     * Loads the project context .pro.
     * <p>
     * Does however not load the scenes (only the scene names as reference) or
     * meshes/textures (see ProjectManager).
     *
     * @param ref project to load
     * @return loaded project context without scenes
     */
    public ProjectContext loadProjectContext(ProjectRef ref) {
        var searchFolder = new File(ref.getPath());
        if (!searchFolder.exists()) {
            return null;
        }

        var projectFile = FileUtils.listFiles(searchFolder, new String[]{PROJECT_EXTENSION}, false).stream()
                .findFirst()
                .orElse(null);

        if (projectFile != null) {
            try (var fis = new FileInputStream(projectFile)) {
                var res = mapper.readValue(fis, ProjectContext.class);
                res.path = ref.getPath();
                return res;
            } catch (Exception e) {
                log.error("ERROR", e);
            }
        }

        return null;
    }
}
