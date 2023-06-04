package com.mbrlabs.mundus.editor.events;

import com.mbrlabs.mundus.commons.assets.Asset;
import lombok.Getter;

@Getter
public class AssetImportEvent implements Event {

    private final Asset<?> asset;

    public AssetImportEvent(Asset<?> asset) {
        this.asset = asset;
    }

    public interface AssetImportListener {
        @Subscribe
        void onAssetImported(AssetImportEvent event);
    }
}
