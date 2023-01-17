package com.mbrlabs.mundus.commons.assets.terrain;

import com.badlogic.gdx.utils.FloatArray;
import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.FileInputStream;

@Slf4j
public class TerrainAssetLoader implements AssetLoader<TerrainAsset, TerrainMeta> {
    @Override
    public TerrainAsset load(Meta<TerrainMeta> meta) {
        var asset = new TerrainAsset(meta);
        final FloatArray floatArray = new FloatArray();

        var terrainFile = meta.getFile().child(meta.getAdditional().getTerrainFile());

        try (var is = new DataInputStream(new FileInputStream(terrainFile.file()))) {
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
        } catch (Exception e) {
            log.error("ERROR", e);
            return null;
        }

        return asset;
    }

}
