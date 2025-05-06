package net.nevinsky.abyssus.lib.assets.gltf.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.nevinsky.abyssus.lib.assets.gltf.extension.Extensions;

import java.io.IOException;

public class GlTFExtensionsSerializer extends JsonSerializer<Extensions> {
    @Override
    public void serialize(Extensions value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.getCodec().writeValue(gen, value.getValues());
    }
}
