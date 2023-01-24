package com.mbrlabs.mundus.commons.importer;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;

import java.io.IOException;

public class CameraSerializer extends StdSerializer<PerspectiveCamera> {
    public CameraSerializer() {
        super((Class<PerspectiveCamera>) null);
    }

    @Override
    public void serialize(PerspectiveCamera value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("viewPointPosition", new Vector3Dto(value.direction));
        gen.writeObjectField("position", new Vector3Dto(value.position));
        gen.writeNumberField("far", value.far);
        gen.writeNumberField("near", value.near);
        gen.writeNumberField("fieldOfView", value.fieldOfView);
        gen.writeEndObject();
    }
}
