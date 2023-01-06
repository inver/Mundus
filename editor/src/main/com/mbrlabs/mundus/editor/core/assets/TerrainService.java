package com.mbrlabs.mundus.editor.core.assets;

import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainMeta;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

@Component
@RequiredArgsConstructor
public class TerrainService {

    private final EditorCtx editorCtx;
    private final TerrainAssetLoader terrainAssetLoader;
    private final AssetsStorage assetsStorage;
    private final MetaService metaService;

    @SneakyThrows
    public TerrainAsset createTerrainAsset(String name, int vertexResolution, int size) {
        var terraFilename = name + ".terrain";

        var terraFile = assetsStorage.getAssetsFolder().child(name).child(terraFilename).file();
        FileUtils.touch(terraFile);

        var terrainMeta = new TerrainMeta();
        terrainMeta.setSize(size);
        terrainMeta.setUv(60f);
        terrainMeta.setTerrainFile(terraFilename);

        var meta = new Meta<TerrainMeta>();
        meta.setType(AssetType.TERRAIN);
        meta.setAdditional(terrainMeta);
        meta.setFile(assetsStorage.getAssetsFolder().child(name));
        metaService.save(meta);

//
        // create initial height data
        var data = new float[vertexResolution * vertexResolution];

        // write terra file
        try (var outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(terraFile)))) {
            for (var f : data) {
                outputStream.writeFloat(f);
            }
        }

        var asset = terrainAssetLoader.load(meta);

        //todo
        // set base texture
//        var chessboard = findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD)
//        if (chessboard != null) {
//            asset.splatBase = chessboard as TextureAsset
//            asset.applyDependencies()
//            metaSaver.save(asset.meta)
//        }

//        addAsset(asset);
        return asset;
    }
}
