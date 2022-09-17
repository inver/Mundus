package com.mbrlabs.mundus.commons.assets.material;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.assets.AssetService;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.meta.dto.Meta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class MaterialService implements AssetService<MaterialAsset> {

    private final MetaService metaService;

    @Override
    public void save(MaterialAsset asset) {
        var props = new Properties();
        if (asset.getDiffuseColor() != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_COLOR, asset.getDiffuseColor().toString());
        }
        if (asset.getDiffuseTexture() != null) {
            props.setProperty(MaterialAsset.PROP_DIFFUSE_TEXTURE, asset.getDiffuseTexture().getID());
        }
        if (asset.getNormalMap() != null) {
            props.setProperty(MaterialAsset.PROP_MAP_NORMAL, asset.getNormalMap().getID());
        }
        props.setProperty(MaterialAsset.PROP_OPACITY, asset.getOpacity() + "");
        props.setProperty(MaterialAsset.PROP_SHININESS, asset.getShininess() + "");

        try (var fos = new FileOutputStream(asset.getFile().file())) {
            props.store(fos, null);
            fos.flush();

            // save meta file
            metaService.save(asset.getMeta());
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }

    @Override
    public MaterialAsset load(Meta meta, FileHandle assetFile) {
        var asset = new MaterialAsset(meta, assetFile);
        asset.load();
        return asset;
    }

    @Override
    public void copy(FileHandle from, FileHandle to) {

    }
}
