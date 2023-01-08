package com.mbrlabs.mundus.commons.terrain;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.FileInputStream;

@Slf4j
public class TerrainService {
    public Terrain createFromAsset(TerrainAsset asset) {
        final FloatArray floatArray = new FloatArray();
        var meta = asset.getMeta();

        var terrainFile = meta.getFile().child(meta.getAdditional().getTerrainFile());

        try (var is = new DataInputStream(new FileInputStream(terrainFile.file()))) {
            while (is.available() > 0) {
                floatArray.add(is.readFloat());
            }
        } catch (Exception e) {
            log.error("ERROR", e);
            return null;
        }

        var terrain = new Terrain(asset, asset.getMeta().getAdditional().getSize(), floatArray.toArray());
        terrain.init();
        terrain.updateUvScale(new Vector2(meta.getAdditional().getUv(), meta.getAdditional().getUv()));
        terrain.update();
        return terrain;
    }
}
