package com.mbrlabs.mundus.editor.core.assets;

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class EditorAssetManagerTest extends BaseCtxTest {
    @Autowired
    private EditorAssetManager assetManager;

    @Test
    public void testLoadBundledAssets() {
        var assets = new HashMap<AssetKey, Asset<?>>();
        assetManager.loadStandardAssets(assets);

        Assert.assertEquals(22, assets.size());
    }
}
