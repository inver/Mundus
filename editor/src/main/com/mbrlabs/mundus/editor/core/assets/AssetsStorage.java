package com.mbrlabs.mundus.editor.core.assets;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAssetLoader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderMeta;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.FileFilter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.BUNDLED_FOLDER;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetsStorage {

    private final AssetWriter assetWriter;
    private final ShaderAssetLoader shaderAssetLoader;
    private final EditorCtx ctx;

    public Map<String, Asset> load(ProjectRef ref) {
        var res = new HashMap<String, Asset>();
        res.putAll(loadDefault());
//        res.putAll();
        return res;
    }

    //todo load from zip or jar packages
    public void loadAssets(FileHandle rootFolder, boolean isRuntime) {
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
//        for (FileHandle meta : metaFiles) {
//            var asset = loadAsset(metaFileService.load(meta));
//            if (listener != null) {
//                listener.onLoad(asset, assets.size(), metaFiles.size());
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


    public Map<String, Asset> loadDefault() {
        var res = new HashMap<String, Asset>();
        loadByType(res, BUNDLED_FOLDER + "textures", AssetType.TEXTURE);
        return res;
    }

    private void loadByType(Map<String, Asset> map, String path, AssetType type) {
        try {
            for (FileHandle file : Gdx.files.internal(path).list()) {
                Asset asset = null;
                if (type == AssetType.TEXTURE) {
                    asset = loadExistingTextureAsset(file);
                }

                if (asset != null) {
                    map.put(asset.getID(), asset);
                }
            }
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    public FileHandle getAssetsFolder() {
        return new FileHandle(ctx.getCurrent().path).child("assets");
    }

    @SneakyThrows
    public ShaderAsset createShader() {
        var id = ctx.getCurrent().obtainID();
        var assetName = "Shader_" + id;

        var path = getAssetsFolder().child(assetName);
        FileUtils.forceMkdir(path.file());

        var commonMeta = new Meta<ShaderMeta>();
        commonMeta.setFile(path);
        commonMeta.setType(AssetType.SHADER);
        commonMeta.setAdditional(new ShaderMeta());

        var asset = new ShaderAsset(commonMeta);
        asset.setFragmentShader("");
        asset.setVertexShader("");
        assetWriter.writeAsset(asset);

        return shaderAssetLoader.load(commonMeta);
    }

    private TextureAsset loadExistingTextureAsset(FileHandle file) {
//        var meta = createMeta(file, AssetType.TEXTURE);
//        return textureService.load(meta, file);
        return null;
    }

    private Meta createMeta(FileHandle file, AssetType type) {
//        var res = new Meta(file);
//        res.setUuid(clearedUUID());
//        res.setVersion(Meta.CURRENT_VERSION);
//        res.setLastModified(System.currentTimeMillis());
//        res.setType(type);
//        return res;
        return null;
    }

    private String clearedUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public FileHandle loadAssetFile(String path) {
        return Gdx.files.internal(path);
    }

}
