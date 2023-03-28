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

package com.mbrlabs.mundus.commons.assets;

import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.scene3d.components.AssetUsage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

/**
 * A generic asset type.
 * <p>
 * Assets hold a file handle to the asset file. They also have a meta file,
 * which contains meta information about the asset. Assets can have dependencies
 * to other assets by specifying the asset id in the meta file or in the asset
 * file.
 *
 * @author Marcus Brummer
 * @version 01-10-2016
 */
@RequiredArgsConstructor
public abstract class Asset<M> implements Disposable, AssetUsage {

    @Getter
    protected final Meta<M> meta;

    public AssetType getType() {
        return meta.getType();
    }

    public String getName() {
        return meta.getFile().name();
    }

    public String getID() {
        return meta.getUuid();
    }

    @Override
    public String toString() {
        return "[" + getMeta().getType().toString() + "] " + getName();
    }

    /**
     * Loads the asset.
     * <p>
     * Loads the asset from disk and creates it.
     */
    @Deprecated
    //todo remove this method
    public void load() {
        throw new UnsupportedOperationException("Load asset from asset is not supported! User Loader instead of");
    }

    /**
     * Resolves all dependencies of this asset.
     * <p>
     * Before calling this method all assets must have been loaded using the
     * load() method.
     *
     * @param assets map of loaded assets with asset id as key
     */
    @Deprecated
    public abstract void resolveDependencies(Map<String, Asset> assets);

    /**
     * Applies dependent assets to this one.
     * <p>
     * If dependencies have been set, this method applies them to the asset.
     * Note, that the asset and all it's set dependencies must have called
     * load() before calling this method.
     */
    @Deprecated
    public abstract void applyDependencies();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Asset<?> asset = (Asset<?>) o;

        return new EqualsBuilder().append(meta, asset.meta).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(meta).toHashCode();
    }
}
