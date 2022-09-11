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

package com.mbrlabs.mundus.commons.mapper;

import com.badlogic.gdx.graphics.Color;
import com.mbrlabs.mundus.commons.dto.BaseLightDto;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;

/**
 * The converter for lights.
 */
public class BaseLightConverter {

    /**
     * Converts {@link BaseLightDto} to {@link BaseLight}.
     */
    public static BaseLight convert(BaseLightDto dto) {
        if (dto == null) return null;
        BaseLight light = new BaseLight();
        light.intensity = dto.getIntensity();
        light.color.set(dto.getColor());

        return light;
    }

    /**
     * Converts {@link BaseLight} to {@link BaseLightDto}.
     */
    public static BaseLightDto convert(BaseLight light) {
        if (light == null) {
            return null;
        }

        return new BaseLightDto(light.intensity, Color.rgba8888(light.color));
    }
}
