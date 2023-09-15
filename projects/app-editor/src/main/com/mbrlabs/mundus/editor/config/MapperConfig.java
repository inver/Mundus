package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mbrlabs.mundus.commons.importer.CameraDeserializer;
import com.mbrlabs.mundus.commons.importer.CameraSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS;

@Configuration
public class MapperConfig {
    @Bean
    public ObjectMapper mapper() {
        var res = new ObjectMapper();
        res.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        res.configure(ALLOW_NON_NUMERIC_NUMBERS, true);

        var appModule = new SimpleModule();
        appModule.addSerializer(PerspectiveCamera.class, new CameraSerializer());
        appModule.addDeserializer(PerspectiveCamera.class, new CameraDeserializer());

        res.registerModule(appModule);
        return res;
    }
}
