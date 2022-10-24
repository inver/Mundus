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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonWriter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
public class ProjectSettings {

    private ExportSettings export = new ExportSettings();

    public ExportSettings getExport() {
        return export;
    }

    /**
     *
     */
    public class ExportSettings {
        public JsonWriter.OutputType jsonType = JsonWriter.OutputType.json;
        public boolean compressScenes = false;
        public boolean allAssets = true;
        public FileHandle outputFolder;
    }


}
