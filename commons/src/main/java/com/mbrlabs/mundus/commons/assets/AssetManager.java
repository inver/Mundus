package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.exceptions.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.exceptions.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.material.MaterialService;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.meta.dto.Meta;
import com.mbrlabs.mundus.commons.assets.model.ModelService;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureService;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainService;
import com.mbrlabs.mundus.commons.assets.texture.TextureService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.io.FileFilter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class AssetManager implements Disposable {

    @Getter
    protected final List<Asset> assets = new ArrayList<>();
    @Getter
    protected final Map<String, Asset> assetIndex = new HashMap<>();

    protected final FileHandle rootFolder;

    protected final MetaService metaService;
    protected final TextureService textureService;
    protected final TerrainService terrainService;
    protected final MaterialService materialService;
    protected final PixmapTextureService pixmapTextureService;
    protected final ModelService modelService;

    public void addAsset(Asset asset) {
        if (asset == null) {
            log.debug("Skip added null asset");
            return;
        }
        if (assetIndex.get(asset.getID()) == null) {
            assets.add(asset);
            assetIndex.put(asset.getID(), asset);
        } else {
            log.debug("Asset with id '{}' already added", asset.getID());
        }
    }

    public List<Asset> getAssetsByType(AssetType type) {
        return assets.stream()
                .filter(asset -> asset.getType() == type)
                .collect(Collectors.toList());
    }

    private Asset loadAsset(Meta meta) throws MetaFileParseException, AssetNotFoundException {
        // get handle to asset
        //   String assetPath = meta.getFile().pathWithoutExtension();
        FileHandle assetFile = meta.getFile().sibling(meta.getFile().nameWithoutExtension());

        // check if asset exists
        if (!assetFile.exists()) {
            throw new AssetNotFoundException("Meta file found, but asset does not exist: " + meta.getFile().path());
        }

        // load actual asset
        Asset asset;
        switch (meta.getType()) {
            case TEXTURE:
                asset = textureService.load(meta, assetFile);
                break;
            case PIXMAP_TEXTURE:
                asset = pixmapTextureService.load(meta, assetFile);
                break;
            case TERRAIN:
                asset = terrainService.load(meta, assetFile);
                break;
            case MODEL:
                asset = modelService.load(meta, assetFile);
                break;
            case MATERIAL:
                asset = materialService.load(meta, assetFile);
                break;
            case SKYBOX_HDR:
            case SKYBOX:
                throw new NotImplementedException();
            default:
                return null;
        }

        addAsset(asset);
        return asset;
    }

    @Override
    public void dispose() {

    }

    public void loadAssets(AssetLoadingListener listener, boolean isRuntime) {
        // create meta file filter
        FileFilter metaFileFilter = file -> file.getName().endsWith(Meta.META_EXTENSION);

        List<FileHandle> metaFiles;

        if (isRuntime && Gdx.app.getType() == Application.ApplicationType.Desktop) {
            // Desktop applications cannot use .list() for internal jar files.
            // Application will need to provide an assets.txt file listing all Mundus assets
            // in the Mundus root directory.
            // https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/
            var fileList = rootFolder.child("assets.txt");
            var files = fileList.readString().split("\\n");

            // Get meta file extension file names
            metaFiles = Stream.of(files)
                    .filter(name -> name.endsWith(Meta.META_EXTENSION))
                    .map(rootFolder::child)
                    .collect(Collectors.toList());
        } else {
            metaFiles = Arrays.asList(rootFolder.list(metaFileFilter));
        }

        // load assets
        for (FileHandle meta : metaFiles) {
            var asset = loadAsset(metaService.load(meta));
            if (listener != null) {
                listener.onLoad(asset, assets.size(), metaFiles.size());
            }
        }

//        // resolve material assets
//        for (Asset asset : assets) {
//            if (asset instanceof MaterialAsset) {
//                asset.resolveDependencies(assetIndex);
//                asset.applyDependencies();
//            }
//        }

        // resolve other assets
        for (Asset asset : assets) {
//            if (asset instanceof MaterialAsset) continue;
            asset.resolveDependencies(assetIndex);
            asset.applyDependencies();
        }

        if (listener != null) {
            listener.onFinish(assets.size());
        }
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
