package com.mbrlabs.mundus.commons.core.ecs.serialization;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableSceneObject;

public class RenderableObjectSerializer implements JsonSerializer<RenderableSceneObject> {
    @Override
    public void write(Json json, RenderableSceneObject object, Class knownType) {
        json.writeValue(object.toDto());
    }

    @Override
    public RenderableSceneObject read(Json json, JsonValue jsonData, Class type) {
        throw new IllegalStateException("This method should never be called!");
    }
}
