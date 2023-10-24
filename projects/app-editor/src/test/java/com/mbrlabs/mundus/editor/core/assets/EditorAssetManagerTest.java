package com.mbrlabs.mundus.editor.core.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AppFileHandle;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.editor.config.AppEnvironment;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EditorAssetManagerTest extends BaseCtxTest {
    @Autowired
    private EditorAssetManager assetManager;
    @Autowired
    private AppEnvironment environment;
    @Autowired
    private EditorCtx ctx;

    @Test
    public void testLoadBundledAssets() {
        var assets = new HashMap<AssetKey, Asset<?>>();
        assetManager.loadStandardAssets(assets);

        assertEquals(23, assets.size());
    }

    @Test
    void testSeparateModelAsset() {
//        ModelAsset asset = assetManager.loadAsset(new AppFileHandle("assets/input/model", Files.FileType.Classpath));
//        ModelAsset resAsset = assetManager.addAssetToProject(asset);
//
//        var projectFolder = new File(ctx.getCurrent().getPath() + "/assets");
//        var materials = resAsset.getMeta().getAdditional().getMaterials();
//        var meta = resAsset.getMeta().getFile();
//        materials.forEach(mat -> checkMaterial(meta.child(mat)));
    }

    @Disabled
    @Test
    void testSeparateTerrainAsset() {

    }

    @Test
    void testSeparateMaterialAsset() {

    }

    @Test
    void testSeparateSkyboxAsset() {

    }

    private void checkMaterial(FileHandle handle) {
        Assertions.assertTrue(handle.exists());

        MaterialAsset asset = assetManager.loadAsset(handle);
        var texture = asset.getMeta().getAdditional().getDiffuseTexture();
        Assertions.assertTrue(texture.startsWith("asset$"));
        var textureName = texture.substring(6);
        var textureHandle = asset.getMeta().getFile().child(textureName);
        checkTexture(textureHandle);
    }

    private void checkTexture(FileHandle handle) {
        Assertions.assertTrue(handle.exists());

        TextureAsset asset = assetManager.loadAsset(handle);
        Assertions.assertNotNull(asset.getTexture());
    }
}
