package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.exceptions.AssetTypeNotSupportException;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

@Slf4j
@RequiredArgsConstructor
public class AssetManager {

    protected final ObjectMapper mapper;
    protected final MetaService metaService;
    protected final TextureAssetLoader textureService;
    protected final TerrainAssetLoader terrainAssetLoader;
    protected final MaterialAssetLoader materialService;
    protected final PixmapTextureAssetLoader pixmapTextureService;
    protected final ModelAssetLoader modelService;
    protected final ShaderAssetLoader shaderService;
    protected final SkyboxAssetLoader skyboxAssetLoader;

    @SuppressWarnings("unchecked")
    public <T extends Asset<?>> T loadAsset(FileHandle assetFolderPath) {
        var meta = metaService.loadCommon(assetFolderPath);
        switch (meta.getType()) {
            case MODEL:
                return (T) modelService.load(metaService.loadModelMeta(assetFolderPath));
            case MATERIAL:
                return (T) materialService.load(metaService.loadMaterialMeta(assetFolderPath));
            case TEXTURE:
                return (T) textureService.load(metaService.loadTextureMeta(assetFolderPath));
            case PIXMAP_TEXTURE:
                return (T) pixmapTextureService.load(metaService.loadPixmapMeta(assetFolderPath));
            case SHADER:
                return (T) shaderService.load(metaService.loadShaderMeta(assetFolderPath));
            case SKYBOX:
                return (T) skyboxAssetLoader.load(metaService.loadSkyboxMeta(assetFolderPath));
            case TERRAIN:
                return (T) terrainAssetLoader.load(metaService.loadTerrainMeta(assetFolderPath));
            default:
                throw new AssetTypeNotSupportException("Asset with type '" + meta.getType() + "' not supported");
        }
    }

    public <T extends Asset<?>> T loadCurrentProjectAsset(String assetName) {
        throw new NotImplementedException("errm, needed path of project/game/etc");
    }
}
