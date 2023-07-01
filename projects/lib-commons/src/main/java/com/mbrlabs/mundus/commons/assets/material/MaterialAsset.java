/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.assets.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Marcus Brummer
 * @version 09-10-2016
 */
@Slf4j
@Getter
@Setter
public class MaterialAsset extends Asset<MaterialMeta> {

    private Color diffuseColor = Color.WHITE;
    private float shininess = 0f;
    private float opacity = 1f;

    private Texture diffuseTexture = null;
    private Texture ambientOcclusionTexture = null;
    private Texture albedoTexture = null;
    private Texture heightTexture = null;
    private Texture metallicTexture = null;
    private Texture normalTexture = null;
    private Texture roughnessTexture = null;

    public MaterialAsset(Meta meta) {
        super(meta);
    }

    /**
     * Applies this material asset to the libGDX material.
     *
     * @param material
     * @return
     */
    public Material applyToMaterial(Material material) {
        if (diffuseColor != null) {
            material.set(new ColorAttribute(ColorAttribute.Diffuse, diffuseColor));
        }
        if (diffuseTexture != null) {
            material.set(new TextureAttribute(TextureAttribute.Diffuse, diffuseTexture));
        } else {
            material.remove(TextureAttribute.Diffuse);
        }
//        if (diffuseColor != null) {
//            material.set(new ColorAttribute(ColorAttribute.Diffuse, diffuseColor));
//        }
//        if (diffuseTexture != null) {
//            material.set(new TextureAttribute(TextureAttribute.Diffuse, diffuseTexture.getTexture()));
//        } else {
//            material.remove(TextureAttribute.Diffuse);
//        }
//        material.set(new FloatAttribute(FloatAttribute.Shininess, shininess));
        return material;
    }

    public Color getDiffuseColor() {
        return diffuseColor;
    }

    @Override
    public void dispose() {
        // nothing to dispose
    }

    public String getPreview() {
        return meta.getAdditional().getPreview();
    }
}
