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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
public class ProjectSettings {

    private final ExportSettings export = new ExportSettings();

    public ExportSettings getExport() {
        return export;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSettings that = (ProjectSettings) o;

        return new EqualsBuilder().append(export, that.export).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(export).toHashCode();
    }

    /**
     *
     */
    public static class ExportSettings {
        public JsonWriter.OutputType jsonType = JsonWriter.OutputType.json;
        public boolean compressScenes = false;
        public boolean allAssets = true;
        public FileHandle outputFolder;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            ExportSettings that = (ExportSettings) o;

            return new EqualsBuilder()
                    .append(compressScenes, that.compressScenes)
                    .append(allAssets, that.allAssets)
                    .append(jsonType, that.jsonType)
                    .append(outputFolder, that.outputFolder)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(jsonType)
                    .append(compressScenes)
                    .append(allAssets)
                    .append(outputFolder)
                    .toHashCode();
        }
    }


}
