/*
 * Copyright (c) 2021. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.dto;

import com.mbrlabs.mundus.commons.env.fog.Fog;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import lombok.Data;

/**
 * @author Tibor Zsuro
 * @version 12-08-2021
 */
@Data
public class SceneDto {
    private long id;
    private String name;

    private boolean ambientLightEnabled;
    private BaseLight ambientLight;

    private boolean fogEnabled;
    private Fog fog;

    private boolean skyboxEnabled;
    private String skyboxName;

    private Object ecs;
}
