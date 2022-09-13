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
import com.mbrlabs.mundus.commons.env.lights.*;

/**
 * @author Marcus Brummer
 * @version 04-01-2016
 */
public class AppEnvironment extends Environment {

    private Fog fog;
    private BaseLight ambientLight;

    public AppEnvironment() {
        super();
        ambientLight = new BaseLight();
        fog = null;
    }

    public AppEnvironment add(SunLight light) {
        var sunLights = (SunLightsAttribute) get(SunLightsAttribute.Type);
        if (sunLights == null) {
            sunLights = new SunLightsAttribute();
            set(sunLights);
        }
        sunLights.lights.add(light);

        return this;
    }

    public AppEnvironment add(DirectionalLight light) {
        DirectionalLightsAttribute dirLights = ((DirectionalLightsAttribute) get(DirectionalLightsAttribute.TYPE));
        if (dirLights == null) {
            set(dirLights = new DirectionalLightsAttribute());
        }
        dirLights.lights.add(light);

        return this;
    }

    public AppEnvironment remove(SunLight light) {
        SunLightsAttribute sunLights = ((SunLightsAttribute) get(SunLightsAttribute.Type));
        if (sunLights != null) {
            sunLights.lights.removeValue(light, true);
        }
        return this;
    }

    public AppEnvironment remove(DirectionalLight light) {
        DirectionalLightsAttribute dirLights = ((DirectionalLightsAttribute) get(DirectionalLightsAttribute.TYPE));
        if (dirLights != null) {
            dirLights.lights.removeValue(light, true);
        }
        return this;
    }

    public BaseLight getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(BaseLight ambientLight) {
        this.ambientLight = ambientLight;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }
}
