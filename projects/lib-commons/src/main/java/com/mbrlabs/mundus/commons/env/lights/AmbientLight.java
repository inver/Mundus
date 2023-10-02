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

package com.mbrlabs.mundus.commons.env.lights;

import com.badlogic.gdx.graphics.Color;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Marcus Brummer
 * @version 14-02-2016
 */
@Getter
public class AmbientLight {

    protected final Color color = new Color(1, 1, 1, 1);
    protected float intensity = 1f;

    public AmbientLight() {
    }

    public AmbientLight(final AmbientLight copyFrom) {
        intensity = copyFrom.intensity;
        color.set(copyFrom.color);
    }

    public AmbientLight copy() {
        return new AmbientLight(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AmbientLight AmbientLight = (AmbientLight) o;

        return new EqualsBuilder().append(intensity, AmbientLight.intensity).append(color, AmbientLight.color)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(color).append(intensity).toHashCode();
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }
}
