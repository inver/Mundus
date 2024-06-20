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

package com.mbrlabs.mundus.commons.assets.texture;

import com.badlogic.gdx.graphics.Texture;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.utils.TextureProvider;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Marcus Brummer
 * @version 01-10-2016
 */
@Getter
@Setter
public class TextureAsset extends Asset<TextureMeta> implements TextureProvider {

    private Texture texture;

    private float size = 0f;

    public TextureAsset(Meta<TextureMeta> meta) {
        super(meta);
    }

    public boolean isGenerateMipMaps() {
        return meta.getAdditional().isGenerateMipMaps();
    }

    public boolean isTileable() {
        return meta.getAdditional().isTileable();
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
