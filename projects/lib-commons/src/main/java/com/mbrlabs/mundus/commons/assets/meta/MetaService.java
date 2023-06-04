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

package com.mbrlabs.mundus.commons.assets.meta;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.exceptions.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.material.MaterialMeta;
import com.mbrlabs.mundus.commons.assets.model.ModelMeta;
import com.mbrlabs.mundus.commons.assets.shader.ShaderMeta;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxMeta;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainMeta;
import com.mbrlabs.mundus.commons.assets.texture.TextureMeta;
import com.mbrlabs.mundus.commons.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileWriter;

import static com.mbrlabs.mundus.commons.assets.AssetConstants.META_FILE_NAME;

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
@Slf4j
@RequiredArgsConstructor
public class MetaService {

    private final ObjectMapper mapper;

    public Meta loadCommon(FileHandle assetFolderPath) {
        var metaHandle = assetFolderPath.child(META_FILE_NAME);
        if (!metaHandle.exists()) {
            throw new AssetNotFoundException(META_FILE_NAME + " not found in asset folder: " + assetFolderPath);
        }

        return loadCommonMeta(metaHandle);
    }

    private Meta loadCommonMeta(FileHandle handle) {
        if (handle.type() == Files.FileType.Classpath) {
            return FileUtils.readFromClassPath(mapper, handle, Meta.class).withFile(handle.parent());
        }

        return FileUtils.readFromFileSystem(mapper, handle, Meta.class).withFile(handle.parent());
    }

    public Meta<MaterialMeta> loadMaterialMeta(FileHandle assetFolderPath) {
        var metaHandle = assetFolderPath.child(META_FILE_NAME);
        return loadMeta(new TypeReference<>() {
        }, metaHandle);
    }

    public Meta<TextureMeta> loadTextureMeta(FileHandle assetFolderPath) {
        var metaHandle = assetFolderPath.child(META_FILE_NAME);
        return loadMeta(new TypeReference<>() {
        }, metaHandle);
    }

    public Meta<ModelMeta> loadModelMeta(FileHandle assetFolderPath) {
        var metaHandle = assetFolderPath.child(META_FILE_NAME);
        return loadMeta(new TypeReference<>() {
        }, metaHandle);
    }

    public Meta<ShaderMeta> loadShaderMeta(FileHandle assetFolderPath) {
        var metaHandle = assetFolderPath.child(META_FILE_NAME);
        return loadMeta(new TypeReference<>() {
        }, metaHandle);
    }

    public Meta<SkyboxMeta> loadSkyboxMeta(FileHandle assetFolderPath) {
        var metaHandle = assetFolderPath.child(META_FILE_NAME);
        return loadMeta(new TypeReference<>() {
        }, metaHandle);
    }

    public Meta<TerrainMeta> loadTerrainMeta(FileHandle assetFolderPath) {
        var metaHandle = assetFolderPath.child(META_FILE_NAME);
        return loadMeta(new TypeReference<>() {
        }, metaHandle);
    }

    @SuppressWarnings("unchecked")
    private <T> Meta<T> loadMeta(TypeReference<Meta<T>> tr, FileHandle handle) {
        if (handle.type() == Files.FileType.Classpath) {
            return FileUtils.readFullFromClassPath(mapper, handle, tr).withFile(handle.parent());
        }

        return FileUtils.readFullFromFileSystem(mapper, handle, tr).withFile(handle.parent());
    }


    public void save(Meta<?> meta) {
        try (var fw = new FileWriter(meta.getFile().child(META_FILE_NAME).file())) {
            var res = mapper.writeValueAsString(meta);
            IOUtils.write(res, fw);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }
}
