package com.mbrlabs.mundus.commons.core.ecs.serialization;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableSceneObject;
import com.mbrlabs.mundus.commons.model.ModelService;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RenderableObjectDelegateSerializer implements JsonSerializer<RenderableObjectDelegate> {
    private final AssetManager assetManager;
    private final TerrainService terrainService;
    private final ModelService modelService;

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
        var dto = json.readValue(RenderableSceneObject.Dto.class, jsonData.get("asset"));
        var res = new RenderableObjectDelegate();
        res.setShaderName(jsonData.getString("shaderName"));
        var asset = assetManager.loadCurrentProjectAsset(dto.getAssetName());
        switch (dto.getType()) {
            case TERRAIN:
                res.setAsset(terrainService.createFromAsset((TerrainAsset) asset));
                return res;
            case MODEL:
                res.setAsset(modelService.createFromAsset((ModelAsset) asset));
                return res;
            default:
                log.error("Wrong type of RenderableObject for deserialization {}", dto.getType());
                return null;
        }
    }
}
