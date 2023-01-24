package com.mbrlabs.mundus.commons.importer;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;

import java.io.IOException;

public class CameraDeserializer extends StdDeserializer<PerspectiveCamera> {
    public CameraDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public PerspectiveCamera deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        var res = new PerspectiveCamera();

        var node = p.getCodec().readTree(p);
        res.direction.set(p.getCodec().readValue(node.get("viewPointPosition").traverse(), Vector3Dto.class).toVector());
        res.position.set(p.getCodec().readValue(node.get("position").traverse(), Vector3Dto.class).toVector());
        res.far = p.getCodec().readValue(node.get("far").traverse(), Float.class);
        res.near = p.getCodec().readValue(node.get("near").traverse(), Float.class);
        res.fieldOfView = p.getCodec().readValue(node.get("fieldOfView").traverse(), Float.class);

        return res;
    }
}
