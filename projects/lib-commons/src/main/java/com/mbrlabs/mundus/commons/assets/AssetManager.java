package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.exceptions.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.exceptions.AssetTypeNotSupportException;
import com.mbrlabs.mundus.commons.assets.exceptions.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
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

import java.util.List;

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

    public Asset loadAsset(FileHandle assetFolderPath) {
        var meta = metaService.loadCommon(assetFolderPath);
        switch (meta.getType()) {
            case MODEL:
                return modelService.load(metaService.loadModelMeta(assetFolderPath));
            case MATERIAL:
                return materialService.load(metaService.loadMaterialMeta(assetFolderPath));
            case TEXTURE:
                return textureService.load(metaService.loadTextureMeta(assetFolderPath));
            case SHADER:
                return shaderService.load(metaService.loadShaderMeta(assetFolderPath));
            case SKYBOX:
                return skyboxAssetLoader.load(metaService.loadSkyboxMeta(assetFolderPath));
            case TERRAIN:
                return terrainAssetLoader.load(metaService.loadTerrainMeta(assetFolderPath));
            default:
                throw new AssetTypeNotSupportException("Asset with type '" + meta.getType() + "' not supported");
        }
    }

    public Asset<?> loadCurrentProjectAsset(String assetName) {
        throw new NotImplementedException("errm, needed path of project/game/etc");
    }

    private Asset<?> loadAsset(Meta meta) throws MetaFileParseException, AssetNotFoundException {
        var assetFile = meta.getFile().sibling(meta.getFile().nameWithoutExtension());

        // check if asset exists
        if (!assetFile.exists()) {
            throw new AssetNotFoundException("Meta file found, but asset does not exist: " + meta.getFile().path());
        }

        switch (meta.getType()) {
            case TEXTURE:
                return textureService.load(meta);
            case PIXMAP_TEXTURE:
                return pixmapTextureService.load(meta);
            case TERRAIN:
                return terrainAssetLoader.load(meta);
            case MODEL:
                return modelService.load(meta);
            case MATERIAL:
                return materialService.load(meta);
            case SKYBOX:
                return skyboxAssetLoader.load(meta);
            case SKYBOX_HDR:
            default:
                throw new NotImplementedException();
        }
    }


    public void loadAssets(AssetLoadingListener listener, boolean isRuntime) {
        // create meta file filter
//        FileFilter metaFileFilter = file -> file.getName().endsWith(Meta.META_EXTENSION);

        List<FileHandle> metaFiles;

        if (isRuntime && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            // Desktop applications cannot use .list() for internal jar files.
            // Application will need to provide an assets.txt file listing all Mundus assets
            // in the Mundus root directory.
            // https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/
//            var fileList = rootFolder.child("assets.txt");
//            var files = fileList.readString().split("\\n");
//
//            // Get meta file extension file names
//            metaFiles = Stream.of(files)
//                    .filter(name -> name.endsWith(Meta.META_EXTENSION))
//                    .map(rootFolder::child)
//                    .collect(Collectors.toList());
        } else {
//            metaFiles = Arrays.asList(rootFolder.list(metaFileFilter));
        }

        // load assets
//        for (FileHandle meta : metaFiles) {
//            var asset = loadAsset(metaService.load(meta));
//            if (listener != null) {
////                listener.onLoad(asset, assets.size(), metaFiles.size());
//            }
//        }

//        // resolve material assets
//        for (Asset asset : assets) {
//            if (asset instanceof MaterialAsset) {
//                asset.resolveDependencies(assetIndex);
//                asset.applyDependencies();
//            }
//        }

        // resolve other assets
//        for (Asset asset : assets) {
////            if (asset instanceof MaterialAsset) continue;
//            asset.resolveDependencies(assetIndex);
//            asset.applyDependencies();
//        }
//
//        if (listener != null) {
//            listener.onFinish(assets.size());
//        }
    }

    public interface AssetLoadingListener {
        /**
         * Called if an asset loaded
         *
         * @param asset      loaded asset
         * @param progress   number of already loaded assets
         * @param assetCount total number of assets
         */
        void onLoad(Asset asset, int progress, int assetCount);

        /**
         * Called if all assets loaded.
         *
         * @param assetCount total number of loaded assets
         */
        void onFinish(int assetCount);
    }
}
