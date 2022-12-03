package com.mbrlabs.mundus.editor.core.assets;

import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class EditorAssetManagerTest extends BaseCtxTest {
    @Autowired
    private EditorAssetManager assetManager;

    @Test
    public void testLoadBundledAssets() {
        var assets = new HashMap<String, Asset<?>>();
        assetManager.loadStandardAssets(assets);

        Assert.assertEquals(18, assets.size());
    }
}
