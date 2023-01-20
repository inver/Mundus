package com.mbrlabs.mundus.commons.core.ecs.serialization;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;

public class PerspectiveCameraSerializer implements JsonSerializer<PerspectiveCamera> {
    @Override
    public void write(Json json, PerspectiveCamera value, Class knownType) {
        json.writeObjectStart();
        json.writeValue("viewPointPosition", new Vector3Dto(value.direction));
        json.writeValue("position", new Vector3Dto(value.position));
        json.writeValue("far", value.far);
        json.writeValue("near", value.near);
        json.writeValue("fieldOfView", value.fieldOfView);
        json.writeObjectEnd();
    }

    @Override
    public PerspectiveCamera read(Json json, JsonValue jsonData, Class type) {
        var res = new PerspectiveCamera();

        res.direction.set(json.readValue(Vector3Dto.class, jsonData.get("viewPointPosition")).toVector());
        res.position.set(json.readValue(Vector3Dto.class, jsonData.get("position")).toVector());
        res.far = jsonData.get("far").asFloat();
        res.near = jsonData.get("near").asFloat();
        res.fieldOfView = jsonData.get("fieldOfView").asFloat();

        return res;
    }
}
