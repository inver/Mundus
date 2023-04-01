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

package com.mbrlabs.mundus.commons.env;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.mbrlabs.mundus.commons.env.lights.AmbientLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.env.lights.SunLightsAttribute;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
public class SceneEnvironment extends Environment {
    private Fog fog;
    private AmbientLight ambientLight;
    private String skyboxName;

    //todo refactor this methods to operate with BaseLight
    public SceneEnvironment add(SpotLight light) {
        var sunLights = (SunLightsAttribute) get(SunLightsAttribute.Type);
        if (sunLights == null) {
            sunLights = new SunLightsAttribute();
            set(sunLights);
        }
        sunLights.lights.add(light);

        return this;
    }

    public SceneEnvironment add(DirectionalLight light) {
        DirectionalLightsAttribute dirLights = ((DirectionalLightsAttribute) get(DirectionalLightsAttribute.TYPE));
        if (dirLights == null) {
            set(dirLights = new DirectionalLightsAttribute());
        }
        dirLights.lights.add(light);

        return this;
    }

    public SceneEnvironment remove(SpotLight light) {
        SunLightsAttribute sunLights = ((SunLightsAttribute) get(SunLightsAttribute.Type));
        if (sunLights != null) {
            sunLights.lights.removeValue(light, true);
        }
        return this;
    }

    public SceneEnvironment remove(DirectionalLight light) {
        DirectionalLightsAttribute dirLights = ((DirectionalLightsAttribute) get(DirectionalLightsAttribute.TYPE));
        if (dirLights != null) {
            dirLights.lights.removeValue(light, true);
        }
        return this;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void setSkyboxName(String skyboxName) {
        this.skyboxName = skyboxName;
    }

    public String getSkyboxName() {
        return skyboxName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SceneEnvironment that = (SceneEnvironment) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(fog, that.fog)
                .append(ambientLight, that.ambientLight).append(skyboxName, that.skyboxName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
                .append(fog).append(ambientLight).append(skyboxName).toHashCode();
    }
}
