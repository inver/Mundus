package com.mbrlabs.mundus.commons.assets.material;

import com.badlogic.gdx.graphics.Color;
import com.mbrlabs.mundus.commons.assets.AssetLoader;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MaterialAssetLoader implements AssetLoader<MaterialAsset, MaterialMeta> {

    @Override
    public MaterialAsset load(Meta<MaterialMeta> meta) {
        var asset = new MaterialAsset(meta);
        asset.setDiffuseColor(new Color(meta.getAdditional().getDiffuseColor()));
//        asset.load();
        return asset;
    }

    //        try {
//            Reader reader = file.reader();
//            PropertiesUtils.load(MAP, reader);
//            reader.close();
//            // shininess & opacity
//            try {
//                String value = MAP.get(PROP_SHININESS, null);
//                if (value != null) {
//                    shininess = Float.valueOf(value);
//                }
//                value = MAP.get(PROP_OPACITY, null);
//                if (value != null) {
//                    opacity = Float.valueOf(value);
//                }
//            } catch (NumberFormatException nfe) {
//                nfe.printStackTrace();
//            }
//
//            // diffuse color
//            String diffuseHex = MAP.get(PROP_DIFFUSE_COLOR);
//            if (diffuseHex != null) {
//                diffuseColor = Color.valueOf(diffuseHex);
//            }
//
//            // asset dependencies
//            diffuseTextureID = MAP.get(PROP_DIFFUSE_TEXTURE, null);
//            normalMapID = MAP.get(PROP_MAP_NORMAL, null);
//        } catch (IOException e) {
//            log.error("ERROR", e);
//        }

}
