package com.mbrlabs.mundus.commons.core.ecs.delegate;

import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.mbrlabs.mundus.commons.assets.AssetType;
import lombok.*;

public interface RenderableObject extends RenderableProvider {
    String getAssetName();

    AssetType getType();

    default Dto toDto() {
        return new Dto(getAssetName(), getType());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Dto {
        private String assetName;
        private AssetType type;
    }
}
