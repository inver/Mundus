package com.mbrlabs.mundus.editor.ui.ecs;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RenderableDelegateSerializer implements JsonSerializer<RenderableDelegate> {

    private final Map<String, ModelCreator> creatorMap;

    //todo move this initialization to external class
    public RenderableDelegateSerializer(ModelCreator cameraService, ModelCreator lightService) {
        creatorMap = Map.of(
                lightService.getClazz(), lightService,
                cameraService.getClazz(), cameraService
        );
    }

    @Override
    public void write(Json json, RenderableDelegate object, Class knownType) {
        json.writeObjectStart();
        json.writeValue("class", object.getClass().getName());
        json.writeValue("shaderKey", object.getShaderKey());
        json.writeObjectEnd();
    }

    @Override
    public RenderableDelegate read(Json json, JsonValue jsonData, Class type) {
        var modelCreatorService = creatorMap.get(type.getName());
        if (modelCreatorService == null) {
            log.error("Wrong type of RenderableDelegate for deserialization {}", type.getName());
            return null;
        }

        var dto = json.readValue(RenderableDelegate.Dto.class, jsonData);
        return modelCreatorService.createRenderableDelegate(dto.getShaderKey());
    }
}
