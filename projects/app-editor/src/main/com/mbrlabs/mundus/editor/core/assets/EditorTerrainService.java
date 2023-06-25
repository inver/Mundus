package com.mbrlabs.mundus.editor.core.assets;

import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAssetLoader;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainMeta;
import com.mbrlabs.mundus.commons.assets.texture.TextureAssetLoader;
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent;
import com.mbrlabs.mundus.commons.core.ecs.delegate.RenderableObjectDelegate;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.commons.terrain.TerrainObject;
import com.mbrlabs.mundus.commons.terrain.TerrainService;
import com.mbrlabs.mundus.editor.core.ecs.PickableComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderConstants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class EditorTerrainService extends TerrainService {

    private final EditorCtx ctx;
    private final TerrainAssetLoader terrainAssetLoader;
    private final TextureAssetLoader textureAssetLoader;
    private final AssetsStorage assetsStorage;
    private final MetaService metaService;

    @SneakyThrows
    private TerrainAsset createAndSaveAsset(int vertexResolution, int size) {
        var meta = new Meta<TerrainMeta>();

        var folderName = "terrain_" + meta.getUuid();
        var terraFilename = "terrain.data";

        var terraFile = assetsStorage.getAssetsFolder().child(folderName).child(terraFilename).file();
        FileUtils.touch(terraFile);

        var terrainMeta = new TerrainMeta();
        terrainMeta.setSize(size);
        terrainMeta.setUv(60f);
        terrainMeta.setTerrainFile(terraFilename);

        meta.setType(AssetType.TERRAIN);
        meta.setAdditional(terrainMeta);
        meta.setFile(assetsStorage.getAssetsFolder().child(folderName));
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

//        var chessboardAsset =textureAssetLoader.load(metaService.loadTerrainMeta())
//                // set default base texture
//                var chessboard = findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD)
//        if (chessboard != null) {
//            asset.setSplatBase(); =chessboard as TextureAsset
//            asset.applyDependencies()
//            metaSaver.save(asset.meta)
//        }

        return asset;
    }

    public HierarchyNode createTerrain() {
        var world = ctx.getCurrentWorld();

        var id = world.create();
        var name = "Terrain " + id;

        var asset = createAndSaveAsset(TerrainObject.DEFAULT_VERTEX_RESOLUTION, TerrainObject.DEFAULT_SIZE);

        var terrain = createFromAsset(asset);

        world.edit(id)
                .add(new PositionComponent())
                .add(new RenderableObjectDelegate(terrain, ShaderConstants.TERRAIN).asComponent())
                .add(PickableComponent.of(id, new RenderableObjectDelegate(terrain, ShaderConstants.PICKER)));

        return new HierarchyNode(id, name);
    }
}