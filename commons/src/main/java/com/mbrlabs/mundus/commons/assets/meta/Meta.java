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

package com.mbrlabs.mundus.commons.assets.meta;


import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mbrlabs.mundus.commons.assets.AssetType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
@Getter
@Setter
public class Meta<M> {
    private int version = 1;
    private long lastModified = System.currentTimeMillis();
    private String uuid = UUID.randomUUID().toString();
    private AssetType type;
    private M additional;

    // Path of meta file
    @JsonIgnore
    private FileHandle file;

    public Meta withFile(FileHandle file) {
        this.file = file;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Meta<?> meta = (Meta<?>) o;

        return new EqualsBuilder()
                .append(version, meta.version)
                .append(lastModified, meta.lastModified)
//                .append(uuid, meta.uuid)
                .append(type, meta.type)
                .append(additional, meta.additional)
                .append(file, meta.file)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(version)
                .append(lastModified)
//                .append(uuid)
                .append(type)
                .append(additional)
                .append(file)
                .toHashCode();
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

}
