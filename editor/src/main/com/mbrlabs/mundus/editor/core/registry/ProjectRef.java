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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

/**
 * A reference to a Mundus project, stored in the registry.
 *
 * @author Marcus Brummer
 * @version 07-06-2016
 */
@Setter
@EqualsAndHashCode(of = {"path"})
public class ProjectRef {

    @Getter
    private final String path;

    @JsonCreator
    public ProjectRef(@JsonProperty("path") String path) {
        this.path = path;
    }

    public String getName() {
        return Path.of(path).getFileName().toString();
    }
}
