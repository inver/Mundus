package com.mbrlabs.mundus.commons.core.ecs.serialization;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObject;

public class RenderableObjectSerializer implements JsonSerializer<RenderableObject> {
    @Override
    public void write(Json json, RenderableObject object, Class knownType) {
        json.writeValue(object.toDto());
    }

    @Override
    public RenderableObject read(Json json, JsonValue jsonData, Class type) {
        throw new IllegalStateException("This method should never be called!");
    }
}
