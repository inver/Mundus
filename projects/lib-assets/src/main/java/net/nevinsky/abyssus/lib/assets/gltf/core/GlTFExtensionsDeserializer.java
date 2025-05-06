package net.nevinsky.abyssus.lib.assets.gltf.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import net.nevinsky.abyssus.lib.assets.gltf.GlTFExtensionsConfiguration;
import net.nevinsky.abyssus.lib.assets.gltf.extension.Extensions;

import java.io.IOException;

public class GlTFExtensionsDeserializer extends JsonDeserializer<Extensions> {
    @Override
    public Extensions deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        var res = new Extensions();

        var node = p.getCodec().readTree(p);
        var iterator = node.fieldNames();
        while (iterator.hasNext()) {
            var name = iterator.next();
            if (!GlTFExtensionsConfiguration.supports(name)) {
                continue;
            }

            var value = p.getCodec().readValue(node.get(name).traverse(), GlTFExtensionsConfiguration.getByName(name));
            res.getValues().put(name, value);
        }
        return res;
    }
}
