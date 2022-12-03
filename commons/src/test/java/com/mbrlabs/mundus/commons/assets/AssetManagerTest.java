package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.loader.ModelImporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetManagerTest {

    protected final ObjectMapper mapper = new ObjectMapper();
    protected final MetaService metaService = new MetaService(mapper);
    protected final TextureAssetLoader textureService = new TextureAssetLoader();
    protected final TerrainAssetLoader terrainService = new TerrainAssetLoader();
    protected final MaterialAssetLoader materialService = new MaterialAssetLoader();
    protected final PixmapTextureAssetLoader pixmapTextureService = new PixmapTextureAssetLoader();
    protected final ModelAssetLoader modelService = new ModelAssetLoader(new ModelImporter());
    protected final ShaderAssetLoader shaderAssetLoader = new ShaderAssetLoader();
    protected final SkyboxAssetLoader skyboxAssetLoader = new SkyboxAssetLoader();

    private final AssetManager assetManager = new AssetManager(mapper, metaService, textureService, terrainService,
            materialService, pixmapTextureService, modelService, shaderAssetLoader, skyboxAssetLoader);

    @Before
    public void init() {
        Lwjgl3NativesLoader.load();
        var gl20 = mock(GL20.class);
        Gdx.gl = gl20;
        Gdx.gl20 = gl20;
        Gdx.files = new Lwjgl3Files();
        Gdx.app = mock(Application.class);

        when(Gdx.app.getPreferences(any())).thenReturn(mock(Preferences.class));

        var graphics = mock(Graphics.class);
        Gdx.graphics = graphics;

        when(graphics.getWidth()).thenReturn(1024);
        when(graphics.getHeight()).thenReturn(768);

        Gdx.input = mock(Input.class);

    }

    @Test
    public void testLoadTexture() {
        var file = new AppFileHandle("assets/chessboard", Files.FileType.Classpath);
        var asset = (TextureAsset) assetManager.loadAsset(file);
        Assert.assertNotNull(asset);
        Assert.assertEquals(Files.FileType.Classpath, asset.meta.getFile().type());
        Assert.assertNotNull(asset.getTexture());
        Assert.assertEquals(Texture.TextureFilter.MipMapLinearLinear, asset.getTexture().getMagFilter());
        Assert.assertEquals(Texture.TextureFilter.MipMapLinearLinear, asset.getTexture().getMinFilter());
        Assert.assertTrue(asset.isTileable());
        Assert.assertTrue(asset.isGenerateMipMaps());
        Assert.assertEquals(1, asset.meta.getVersion());
        Assert.assertEquals(1663444124794L, asset.meta.getLastModified());
        Assert.assertEquals(AssetType.TEXTURE, asset.meta.getType());
        Assert.assertEquals("assets/chessboard", asset.meta.getFile().path());
        Assert.assertEquals("chessboard.png", asset.meta.getAdditional().getFile());
    }

    @Test
    public void testLoadMaterial() {
        var file = new AppFileHandle("assets/dented-metal-bl", Files.FileType.Classpath);
        var asset = (MaterialAsset) assetManager.loadAsset(file);
        Assert.assertNotNull(asset);
        Assert.assertEquals("preview.jpg", asset.getPreview());
        Assert.assertEquals(1, asset.meta.getVersion());
        Assert.assertEquals(1663444124794L, asset.meta.getLastModified());
        Assert.assertEquals(AssetType.MATERIAL, asset.getType());
    }

    @Test
    public void testLoadModel() {
        var file = new AppFileHandle("assets/sphere", Files.FileType.Classpath);
        var asset = (ModelAsset) assetManager.loadAsset(file);
        Assert.assertNotNull(asset);
    }
}
