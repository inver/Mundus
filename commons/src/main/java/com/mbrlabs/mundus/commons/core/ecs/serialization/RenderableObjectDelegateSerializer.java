package com.mbrlabs.mundus.commons.core.ecs.serialization;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObject;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RenderableObjectDelegateSerializer implements JsonSerializer<RenderableObjectDelegate> {
    private final AssetManager assetManager;
    private final TerrainService terrainService;

    @Override
    public void write(Json json, RenderableObjectDelegate object, Class knownType) {
        json.writeObjectStart();
        json.writeValue("class", RenderableObjectDelegate.class.getName());
        json.writeValue("shaderName", object.getShaderName());
        json.writeValue("asset", object.getAsset());
        json.writeObjectEnd();
    }

    @Override
    public RenderableObjectDelegate read(Json json, JsonValue jsonData, Class type) {
        var dto = json.readValue(RenderableObject.Dto.class, jsonData.get("asset"));
        var res = new RenderableObjectDelegate();
        res.setShaderName(jsonData.getString("shaderName"));
        switch (dto.getType()) {
            case TERRAIN:
                var asset = assetManager.loadCurrentProjectAsset(dto.getAssetName());
                res.setAsset(terrainService.createFromAsset((TerrainAsset) asset));
                return res;
            case MODEL:

                break;
        }
        log.error("Wrong type of RenderableObject for deserialization {}", dto.getType());
        return null;
    }
}
