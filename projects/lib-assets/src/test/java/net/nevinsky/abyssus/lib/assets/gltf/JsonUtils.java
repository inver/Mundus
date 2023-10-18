package net.nevinsky.abyssus.lib.assets.gltf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.experimental.UtilityClass;
import net.nevinsky.abyssus.lib.assets.gltf.core.GlTFExtensionsDeserializer;
import net.nevinsky.abyssus.lib.assets.gltf.core.GlTFExtensionsSerializer;
import net.nevinsky.abyssus.lib.assets.gltf.extension.Extensions;

@UtilityClass
public class JsonUtils {

    public static ObjectMapper createMapper() {
        var module = new SimpleModule();
        module.addDeserializer(Extensions.class, new GlTFExtensionsDeserializer());
        module.addSerializer(Extensions.class, new GlTFExtensionsSerializer());

        return JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build()
                .registerModule(module);
    }

    public static void compareJson() {

    }
}
