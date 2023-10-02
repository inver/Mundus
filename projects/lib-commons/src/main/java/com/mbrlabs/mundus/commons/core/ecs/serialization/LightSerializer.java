package com.mbrlabs.mundus.commons.core.ecs.serialization;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.mbrlabs.mundus.commons.dto.ColorDto;
import com.mbrlabs.mundus.commons.env.lights.AmbientLight;
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LightSerializer implements JsonSerializer<AmbientLight> {
    @Override
    public void write(Json json, AmbientLight light, Class knownType) {
        json.writeObjectStart();
        json.writeValue("color", new ColorDto(light.getColor()));
        json.writeValue("intensity", light.getIntensity());
        json.writeObjectEnd();
    }

    @Override
    public AmbientLight read(Json json, JsonValue jsonData, Class type) {
        var res = new DirectionalLight();
        res.setColor(json.readValue(ColorDto.class, jsonData.get("color")).toColor());
        res.setIntensity(jsonData.get("intensity").asFloat());
        return res;
    }
}
