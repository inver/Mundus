package com.mbrlabs.mundus.editor.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.exceptions.AssetAlreadyExistsException;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.material.MaterialService;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.meta.MetaTerrain;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.assets.model.ModelService;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureService;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainService;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureService;
import com.mbrlabs.mundus.commons.core.ModelFiles;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class EditorAssetManager extends AssetManager {

    private static final String BUNDLED_FOLDER = "bundled/";

    @Getter
    private final Set<Asset> dirtyAssets = new HashSet<>();
    private final MetaSaver metaSaver = new MetaSaver();

    public EditorAssetManager(@Qualifier("rootFolder") FileHandle rootFolder, MetaService metaFileService, TextureService textureService,
                              TerrainService terrainService, MaterialService materialService,
                              PixmapTextureService pixmapTextureService, ModelService modelService) {
        super(rootFolder, metaFileService, textureService, terrainService, materialService, pixmapTextureService,
                modelService);
        if (rootFolder != null && (!rootFolder.exists() || !rootFolder.isDirectory())) {
            log.error("Folder {} doesn't exist or is not a directory", rootFolder.file());
        }
    }

    public void saveAsset(Asset asset) {
        if (asset instanceof MaterialAsset) {
            materialService.save((MaterialAsset) asset);
        } else if (asset instanceof TerrainAsset) {
            terrainService.save((TerrainAsset) asset);
        } else if (asset instanceof ModelAsset) {
            modelService.save((ModelAsset) asset);
        } else {
            throw new NotImplementedException();
        }
    }

    public void loadStandardAssets() {
        try {
            loadByType(BUNDLED_FOLDER + "textures", AssetType.TEXTURE);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    private void loadByType(String path, AssetType type) {
        for (FileHandle file : Gdx.files.internal(path).list()) {
            if (type == AssetType.TEXTURE) {
                loadNewTextureAsset(file);
            }
        }
    }

    protected TextureAsset loadNewTextureAsset(FileHandle file) {
        var meta = createMetaFileFromAsset(file, AssetType.TEXTURE);
        var importedAssetFile = copyAssetToProjectFolder(file);

        return textureService.load(meta, importedAssetFile);
    }

    private Meta createMetaFileFromAsset(FileHandle file, AssetType type) {
        var metaName = file.name() + "." + Meta.META_EXTENSION;
        var metaPath = FilenameUtils.concat(rootFolder.path(), metaName);
        return createNewMetaFile(new FileHandle(metaPath), type);
    }

    private Meta createNewMetaFile(FileHandle file, AssetType type) {
        if (file.exists()) {
            log.error("\"Tried to create new Meta File that already exists: {}", file.name());
            throw new AssetAlreadyExistsException();
        }

        var meta = new Meta(file);
        meta.setUuid(clearedUUID());
        meta.setVersion(Meta.CURRENT_VERSION);
        meta.setLastModified(System.currentTimeMillis());
        meta.setType(type);
        metaSaver.save(meta);

        return meta;
    }

    public ModelAsset createModelAsset(ModelFiles model) {
        var meta = createMetaFileFromAsset(model.getMain(), AssetType.MODEL);

        // copy model file
        model.copyTo(new FileHandle(rootFolder.path()));

        // load & return asset
        var assetFile = new FileHandle(FilenameUtils.concat(rootFolder.path(), model.name()));
        var asset = new ModelAsset(meta, assetFile);
        asset.load();

        addAsset(asset);
        return asset;
    }

    @SneakyThrows
    public MaterialAsset createMaterialAsset(String name) {
        // create empty material file
        var path = FilenameUtils.concat(rootFolder.path(), name) + MaterialAsset.EXTENSION;
        var matFile = Gdx.files.absolute(path);
        FileUtils.touch(matFile.file());

        var meta = createMetaFileFromAsset(matFile, AssetType.MATERIAL);
        var asset = new MaterialAsset(meta, matFile);
        asset.load();

        saveAsset(asset);
        addAsset(asset);
        return asset;
    }

    public TextureAsset createTextureAsset(FileHandle texture) {
        var meta = createMetaFileFromAsset(texture, AssetType.TEXTURE);
        texture.copyTo(new FileHandle(rootFolder.path()));

        var asset = new TextureAsset(meta, new FileHandle(FilenameUtils.concat(rootFolder.path(), texture.name())));
        // TODO parse special texture instead of always setting them
        asset.setTileable(true);
        asset.generateMipmaps(true);
        asset.load();

        addAsset(asset);
        return asset;
    }

    public PixmapTextureAsset createPixmapTextureAsset(int size) {
        var pixmapFilename = clearedUUID().substring(0, 5) + ".png";
        var metaFilename = pixmapFilename + ".meta";

        // create meta file
        var metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename);
        var meta = createNewMetaFile(new FileHandle(metaPath), AssetType.PIXMAP_TEXTURE);

        // create pixmap
        var pixmapPath = FilenameUtils.concat(rootFolder.path(), pixmapFilename);
        var pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        var pixmapAssetFile = new FileHandle(pixmapPath);
        PixmapIO.writePNG(pixmapAssetFile, pixmap);
        pixmap.dispose();

        // load & return asset
        var asset = new PixmapTextureAsset(meta, pixmapAssetFile);
        asset.load();

        addAsset(asset);
        return asset;
    }

    @SneakyThrows
    public TerrainAsset createTerrainAsset(String name, int vertexResolution, int size) {
        var terraFilename = name + ".terrain";
        var metaFilename = terraFilename + "meta";

        // create meta file
        var metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename);
        var meta = createNewMetaFile(new FileHandle(metaPath), AssetType.TERRAIN);
        meta.setTerrain(new MetaTerrain());
        meta.getTerrain().setSize(size);
        meta.getTerrain().setUv(60f);
        metaSaver.save(meta);

        // create terra file
        var terraPath = FilenameUtils.concat(rootFolder.path(), terraFilename);
        var terraFile = new File(terraPath);
        FileUtils.touch(terraFile);

        // create initial height data
        var data = new float[vertexResolution * vertexResolution];

        // write terra file
        var outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(terraFile)));
        for (var f : data) {
            outputStream.writeFloat(f);
        }
        outputStream.flush();
        outputStream.close();

        // load & apply standard chessboard texture
        var asset = new TerrainAsset(meta, new FileHandle(terraFile));
        asset.load();

        //todo
        // set base texture
//        var chessboard = findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD)
//        if (chessboard != null) {
//            asset.splatBase = chessboard as TextureAsset
//            asset.applyDependencies()
//            metaSaver.save(asset.meta)
//        }

        addAsset(asset);
        return asset;
    }


    private String clearedUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private FileHandle copyAssetToProjectFolder(FileHandle file) {
        var copy = new FileHandle(FilenameUtils.concat(rootFolder.path(), file.name()));
        file.copyTo(copy);
        return copy;
    }

    public void dirty(Asset asset) {
        dirtyAssets.add(asset);
    }
}
