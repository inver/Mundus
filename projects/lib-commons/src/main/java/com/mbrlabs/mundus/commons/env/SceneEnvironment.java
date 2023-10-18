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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.mbrlabs.mundus.commons.env.fog.Fog;
import com.mbrlabs.mundus.commons.env.fog.FogDensityAttribute;
import com.mbrlabs.mundus.commons.env.fog.FogGradientAttribute;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute;
import com.mbrlabs.mundus.commons.env.lights.LightIntensityAttribute;
import com.mbrlabs.mundus.commons.env.lights.SpotLight;
import com.mbrlabs.mundus.commons.env.lights.SunLightsAttribute;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
@Slf4j
public class SceneEnvironment extends Environment {
    private BaseLight ambientLight = new BaseLight();
    private Fog fog = new Fog();
    private boolean skyboxEnabled;
    private String skyboxName;

    public void setAmbientLight(Color color, float intensity) {
        if (ambientLight == null) {
            ambientLight = new BaseLight(color, intensity);
        }
        enableAmbientLight();
        // todo implement intensity of ambient light in default shader
    }

    public BaseLight getAmbientLight() {
        return ambientLight;
    }

    public boolean isAmbientLightEnabled() {
        return has(ColorAttribute.AmbientLight);
    }

    public void disableAmbientLight() {
        remove(ColorAttribute.AmbientLight);
        remove(LightIntensityAttribute.AMBIENT_LIGHT_INTENSITY);
    }

    public void enableAmbientLight() {
        set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight.getColor()));
        set(new LightIntensityAttribute(ambientLight.getIntensity()));
    }

    public void setFog(Color color, float density, float gradient) {
        if (fog == null) {
            fog = new Fog(color, density, gradient);
        }
        enableFog();
    }

    public Fog getFog() {
        return fog;
    }

    public boolean isFogEnabled() {
        return has(ColorAttribute.Fog);
    }

    public void disableFog() {
        remove(ColorAttribute.Fog);
        remove(FogDensityAttribute.FOG_DENSITY);
        remove(FogGradientAttribute.FOG_GRADIENT);
    }

    public void enableFog() {
        set(new ColorAttribute(ColorAttribute.Fog, fog.getColor()));
        set(new FogDensityAttribute(fog.getDensity()));
        set(new FogGradientAttribute(fog.getGradient()));
    }

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

    public boolean isSkyboxEnabled() {
        return skyboxEnabled;
    }

    public void setSkyboxEnabled(boolean skyboxEnabled) {
        this.skyboxEnabled = skyboxEnabled;
    }

    public void setSkyboxName(String skyboxName) {
        this.skyboxName = skyboxName;
    }

    public String getSkyboxName() {
        return skyboxName;
    }
}
