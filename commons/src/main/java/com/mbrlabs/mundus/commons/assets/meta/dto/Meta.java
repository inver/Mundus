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

package com.mbrlabs.mundus.commons.assets.meta.dto;


import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
@Getter
@Setter
public class Meta<M> {
    private int version;
    private long lastModified;
    private String uuid;
    private AssetType type;
    private M additional;
    private FileHandle file;

    public Meta withFile(FileHandle file) {
        this.file = file;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meta meta = (Meta) o;

        return uuid.equals(meta.uuid) && file.equals(meta.file);
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + file.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "version=" + version +
                ", lastModified=" + lastModified +
                ", uuid='" + uuid + '\'' +
                ", type=" + type +
                ", file=" + file +
                '}';
    }


    public static final String META_EXTENSION = "meta";
    public static final int CURRENT_VERSION = 1;

    public static final String JSON_VERSION = "v";
    public static final String JSON_LAST_MOD = "mod";
    public static final String JSON_UUID = "id";
    public static final String JSON_TYPE = "t";
    public static final String JSON_TERRAIN = "ter";
    public static final String JSON_MODEL = "mdl";

}
