package com.mbrlabs.mundus.editor.core.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.AppFileHandle;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetConstants;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.commons.assets.material.MaterialAssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.model.ModelAsset;
import com.mbrlabs.mundus.commons.assets.model.ModelAssetLoader;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAsset;
import com.mbrlabs.mundus.commons.assets.pixmap.PixmapTextureAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAsset;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import com.mbrlabs.mundus.commons.utils.FileUtils;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.project.AssetKey;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectContext;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.BUNDLED_FOLDER;

@Component
@Slf4j
public class EditorAssetManager extends AssetManager {
    private final EditorCtx ctx;
    private final AssetWriter assetWriter;
    @Getter
    private final Set<Asset<?>> dirtyAssets = new HashSet<>();

    public EditorAssetManager(ObjectMapper mapper, MetaService metaService, TextureAssetLoader textureService,
                              TerrainAssetLoader terrainService, MaterialAssetLoader materialService,
                              PixmapTextureAssetLoader pixmapTextureService, ModelAssetLoader modelService,
                              ShaderAssetLoader shaderAssetLoader, EditorCtx ctx, SkyboxAssetLoader skyboxAssetLoader,
                              AssetWriter assetWriter) {
        super(mapper, metaService, textureService, terrainService, materialService, pixmapTextureService, modelService,
                shaderAssetLoader, skyboxAssetLoader);
        this.ctx = ctx;
        this.assetWriter = assetWriter;
    }

    @PostConstruct
    public void init() {
        loadStandardAssets(ctx.getAssetLibrary());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Asset<?>> T loadCurrentProjectAsset(String assetName) {
        return (T) loadProjectAsset(ctx.getCurrent().getPath(), assetName);
    }

    public Asset<?> loadProjectAsset(String projectPath, String assetName) {
        try {
            var assetFolder = new FileHandle(projectPath + "/" + ProjectConstants.PROJECT_ASSETS_DIR + assetName);
            return loadAsset(assetFolder);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        throw new IllegalStateException("Failed to load asset: "
                + projectPath + "/" + ProjectConstants.PROJECT_ASSETS_DIR + assetName);
    }

    void loadStandardAssets(Map<AssetKey, Asset<?>> assets) {
        try {
            var metaPaths = getClasspathMetas(BUNDLED_FOLDER);

            for (var metaPath : metaPaths) {
                var assetFolder = new AppFileHandle(metaPath, Files.FileType.Classpath).parent();
                var asset = loadAsset(assetFolder);
                assets.put(new AssetKey(asset.getType(), asset.getName()), asset);
            }
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    public void loadProjectAssets(ProjectContext project) {
        try {
            var metaPaths = new FileHandle(project.getPath() + "/" + ProjectConstants.PROJECT_ASSETS_DIR);

            for (var assetFolder : metaPaths.list()) {
                var asset = loadAsset(assetFolder);
                project.getProjectAssets().put(new AssetKey(asset.getType(), asset.getName()), asset);
            }
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    private List<String> getClasspathMetas(String root) {
        try {
            var res = new ArrayList<String>();
            for (var meta : FileUtils.getResourceFiles(getClass(), root, AssetConstants.META_FILE_NAME)) {
                if (getClass().getClassLoader().getResource(meta) != null) {
                    res.add(meta);
                }
            }
            return res;
        } catch (Exception e) {
            log.error("ERROR", e);
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <M, T extends Asset<M>> T addAssetToProject(T asset) {
        var assets = separateAsset(asset);
        assets.forEach(assetWriter::writeAsset);
        return (T) assets.get(0);
    }

    /**
     * Method separate complex asset to graph of assets.
     * Ex. Model asset with one material and one texture -> 3 asset: model, material and texture.
     *
     * @param bundle complex asset
     * @return separate assets, which linked by string constants
     */
    public List<Asset<?>> separateAsset(Asset<?> bundle) {
        switch (bundle.getMeta().getType()) {
            case MODEL:
                return separateModelAsset((ModelAsset) bundle);
            case TERRAIN:
                return separateTerrainAsset((TerrainAsset) bundle);
            case MATERIAL:
                return separateMaterialAsset((MaterialAsset) bundle);
            case SKYBOX:
                return separateSkyboxAsset((SkyboxAsset) bundle);
            default:
                return List.of(bundle);
        }
    }

    private List<Asset<?>> separateSkyboxAsset(SkyboxAsset bundle) {
        return null;
    }

    private List<Asset<?>> separateMaterialAsset(MaterialAsset bundle) {

        return null;
    }

    private List<Asset<?>> separateTerrainAsset(TerrainAsset bundle) {
        return null;
    }

    private List<Asset<?>> separateModelAsset(ModelAsset bundle) {
        return Collections.emptyList();
    }


    private Meta createMetaFileFromAsset(FileHandle file, AssetType type) {
        return createNewMetaFile(new FileHandle(getMetaPath(file)), type);
    }

    private String getMetaPath(FileHandle file) {
//        var name = file.name() + "." + Meta.META_EXTENSION;
//        return FilenameUtils.concat(rootFolder.path(), name);
        return "";
    }

    private String getAssetPath(FileHandle file) {
//        return FilenameUtils.concat(rootFolder.path(), file.name());
        return null;
    }

    private <T> Meta<T> createNewMetaFile(FileHandle file, AssetType type) {
//        var meta = new Meta<T>(file);
//        meta.setUuid(clearedUUID());
//        meta.setVersion(Meta.CURRENT_VERSION);
//        meta.setLastModified(System.currentTimeMillis());
//        meta.setType(type);
//        metaService.save(meta);
//
//        return meta;
        return null;
    }

    public ModelAsset createModelAsset(ImportedModel model) {
//        var meta = createMetaFileFromAsset(model.getMain(), AssetType.MODEL);
//
//        // copy model file
//        model.copyTo(new FileHandle(rootFolder.path()));
//
//        // load & return asset
//        var assetFile = new FileHandle(FilenameUtils.concat(rootFolder.path(), model.name()));
//        var asset = new ModelAsset(meta);
//        asset.load();
//
//        addAsset(asset);
//        return asset;

        return null;
    }

    @SneakyThrows
    public MaterialAsset createMaterialAsset(String name) {
        // create empty material file
//        var path = FilenameUtils.concat(rootFolder.path(), name) + MaterialAsset.EXTENSION;
//        var matFile = Gdx.files.absolute(path);
//        FileUtils.touch(matFile.file());
//
//        var meta = createMetaFileFromAsset(matFile, AssetType.MATERIAL);
//        var asset = new MaterialAsset(meta);
//        asset.load();
//
//        saveAsset(asset);
//        addAsset(asset);
//        return asset;
        return null;
    }

    public TextureAsset createTextureAsset(FileHandle texture) {
//        var meta = createMetaFileFromAsset(texture, AssetType.TEXTURE);
//        texture.copyTo(new FileHandle(rootFolder.path()));
//
//        var asset = new TextureAsset(meta, new FileHandle(FilenameUtils.concat(rootFolder.path(), texture.name())));
//        // TODO parse special texture instead of always setting them
//        asset.setTileable(true);
//        asset.generateMipmaps(true);
//        asset.load();
//
//        addAsset(asset);
//        return asset;
        return null;
    }

    public PixmapTextureAsset createPixmapTextureAsset(int size) {
//        var pixmapFilename = clearedUUID().substring(0, 5) + ".png";
//        var metaFilename = pixmapFilename + ".meta";
//
//        // create meta file
//        var metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename);
//        var meta = createNewMetaFile(new FileHandle(metaPath), AssetType.PIXMAP_TEXTURE);
//
//        // create pixmap
//        var pixmapPath = FilenameUtils.concat(rootFolder.path(), pixmapFilename);
//        var pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
//        var pixmapAssetFile = new FileHandle(pixmapPath);
//        PixmapIO.writePNG(pixmapAssetFile, pixmap);
//        pixmap.dispose();
//
//        // load & return asset
//        var asset = new PixmapTextureAsset(meta, pixmapAssetFile);
//        asset.load();
//
//        addAsset(asset);
//        return asset;
        return null;
    }

    @SneakyThrows
    public TerrainAsset createTerrainAsset(String name, int vertexResolution, int size) {
//        var terraFilename = name + ".terrain";
//        var metaFilename = terraFilename + "meta";
//
//        // create meta file
//        var metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename);
//        Meta<TerrainMeta> meta = createNewMetaFile(new FileHandle(metaPath), AssetType.TERRAIN);
//        meta.setAdditional(new TerrainMeta());
//        meta.getAdditional().setSize(size);
//        meta.getAdditional().setUv(60f);
//        metaService.save(meta);
//
//        // create terra file
//        var terraPath = FilenameUtils.concat(rootFolder.path(), terraFilename);
//        var terraFile = new File(terraPath);
//        FileUtils.touch(terraFile);
//
//        // create initial height data
//        var data = new float[vertexResolution * vertexResolution];
//
//        // write terra file
//        var outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(terraFile)));
//        for (var f : data) {
//            outputStream.writeFloat(f);
//        }
//        outputStream.flush();
//        outputStream.close();
//
//        // load & apply standard chessboard texture
//        var asset = new TerrainAsset(meta);
//        asset.load();
//
//        //todo
//        // set base texture
////        var chessboard = findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD)
////        if (chessboard != null) {
////            asset.splatBase = chessboard as TextureAsset
////            asset.applyDependencies()
////            metaSaver.save(asset.meta)
////        }
//
//        addAsset(asset);
//        return asset;

        return null;
    }

    public void deleteAsset(Asset asset) {
//        var objectsUsingAsset = findAssetUsagesInScenes(projectManager, asset)
//        var assetsUsingAsset = findAssetUsagesInAssets(asset)
//
//        if (objectsUsingAsset.isNotEmpty() || assetsUsingAsset.isNotEmpty()) {
//            showUsagesFoundDialog(objectsUsingAsset, assetsUsingAsset)
//            return
//        }
//
//        // continue with deletion
//        assets ?.removeValue(asset, true)
//
//        if (asset.file.extension().equals(FileFormatUtils.FORMAT_3D_GLTF)) {
//            // Delete the additional gltf binary file if found
//            var binPath = asset.file.pathWithoutExtension() + ".bin"
//            var binFile = Gdx.files.getFileHandle(binPath, Files.FileType.Absolute)
//            if (binFile.exists())
//                binFile.delete()
//        }
//
//        if (asset.meta.file.exists())
//            asset.meta.file.delete()
//
//        if (asset.file.exists())
//            asset.file.delete()
    }

//    private fun findAssetUsagesInScenes(projectManager:ProjectManager, asset:Asset):HashMap<GameObject, String>
//
//    {
//        val objectsWithAssets = HashMap < GameObject, String>()
//
//        // we check for usages in all scenes
//        for (sceneName in projectManager.current.scenes) {
//            val scene = projectManager.loadScene(projectManager.current, sceneName)
//            checkSceneForAssetUsage(scene, asset, objectsWithAssets)
//        }
//
//        return objectsWithAssets
//    }


    private String clearedUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private FileHandle copyAssetToProjectFolder(FileHandle file) {
//        var copy = new FileHandle(FilenameUtils.concat(rootFolder.path(), file.name()));
//        file.copyTo(copy);
//        return copy;
        return null;
    }

    public void dirty(int entityId) {
        //todo
//        dirtyAssets.add(asset);
    }

//    public GameObject convert(int goID, String name, Asset asset) {
//        var res = new GameObject(name, goID);
//
//        if (asset.getType() == AssetType.MODEL) {
//            var modelAsset = (ModelAsset) asset;
//
//            var modelComponent = new PickableModelComponent(res, ShaderConstants.PICKER);
//            modelComponent.setModel(modelAsset, true);
//            modelComponent.encodeRayPickColorId();
//
//            res.getComponents().add(modelComponent);
//        } else if (asset.getType() == AssetType.MATERIAL) {
//
//        }
//
//        return res;
//    }


}
