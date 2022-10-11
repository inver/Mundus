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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.exceptions.AssetNotFoundException;
import com.mbrlabs.mundus.commons.assets.exceptions.MetaFileParseException;
import com.mbrlabs.mundus.commons.assets.material.MaterialMeta;
import com.mbrlabs.mundus.commons.assets.model.ModelMeta;
import com.mbrlabs.mundus.commons.assets.shader.ShaderMeta;
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
public class MetaLoader {

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

    @SuppressWarnings("unchecked")
    private <T> Meta<T> loadMeta(TypeReference<Meta<T>> tr, FileHandle handle) {
        if (handle.type() == Files.FileType.Classpath) {
            return FileUtils.readFullFromClassPath(mapper, handle, tr).withFile(handle.parent());
        }

        return FileUtils.readFullFromFileSystem(mapper, handle, tr).withFile(handle.parent());
    }


    public void save(Meta<?> meta) {
        try (var fw = new FileWriter(meta.getFile().file())) {
            var res = mapper.writeValueAsString(meta);
            IOUtils.write(res, fw);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }


    private final JsonReader reader = new JsonReader();


    public Meta load(FileHandle file) throws MetaFileParseException {
        Meta meta = new Meta();
        meta.setFile(file);

        JsonValue json = reader.parse(file);
        parseBasics(meta, json);

        if (meta.getType() == AssetType.TERRAIN) {
            parseTerrain(meta, json.get(Meta.JSON_TERRAIN));
        } else if (meta.getType() == AssetType.MODEL) {
            parseModel(meta, json.get(Meta.JSON_MODEL));
        }

        return meta;
    }

    private void parseBasics(Meta meta, JsonValue jsonRoot) {
        meta.setVersion(jsonRoot.getInt(Meta.JSON_VERSION));
        meta.setLastModified(jsonRoot.getLong(Meta.JSON_LAST_MOD));
        meta.setUuid(jsonRoot.getString(Meta.JSON_UUID));
        meta.setType(AssetType.valueOf(jsonRoot.getString(Meta.JSON_TYPE)));
    }

    private void parseTerrain(Meta<TerrainMeta> meta, JsonValue jsonTerrain) {
        if (jsonTerrain == null) return;

        var terrain = new TerrainMeta();
//        terrain.setSize(jsonTerrain.getInt(MetaTerrain.JSON_SIZE));
//        terrain.setUv(jsonTerrain.getFloat(MetaTerrain.JSON_UV_SCALE, Terrain.DEFAULT_UV_SCALE));
//        terrain.setSplatmap(jsonTerrain.getString(MetaTerrain.JSON_SPLATMAP, null));
//        terrain.setSplatBase(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_BASE, null));
//        terrain.setSplatR(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_R, null));
//        terrain.setSplatG(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_G, null));
//        terrain.setSplatB(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_B, null));
//        terrain.setSplatA(jsonTerrain.getString(MetaTerrain.JSON_SPLAT_A, null));

        meta.setAdditional(terrain);
    }

    private void parseModel(Meta<ModelMeta> meta, JsonValue jsonModel) {
        if (jsonModel == null) return;

        final ModelMeta model = new ModelMeta();
//        final JsonValue materials = jsonModel.get(MetaModel.JSON_DEFAULT_MATERIALS);
//
//        for (final JsonValue mat : materials) {
//            System.out.println(mat.name);
//            final String g3dbID = mat.name;
//            final String assetUUID = materials.getString(g3dbID);
//            model.getMaterials().put(g3dbID, assetUUID);
//        }

        meta.setAdditional(model);
    }

//    @SneakyThrows
//    public void save(Meta meta) {
//        var json = new Json(JsonWriter.OutputType.json);
//
//        json.setWriter(meta.getFile().writer(false));
//
//        json.writeObjectStart();
//        addBasics(meta, json);
//        if (meta.getType() == AssetType.TERRAIN) {
//            addTerrain(meta, json);
//        } else if (meta.getType() == AssetType.MODEL) {
//            addModel(meta, json);
//        }
//        json.writeObjectEnd();
//
//        // Close stream, otherwise file becomes locked
//        json.getWriter().close();
//    }

    private void addBasics(Meta meta, Json json) {
        json.writeValue(Meta.JSON_VERSION, meta.getVersion());
        json.writeValue(Meta.JSON_LAST_MOD, meta.getLastModified());
        json.writeValue(Meta.JSON_TYPE, meta.getType());
        json.writeValue(Meta.JSON_UUID, meta.getUuid());
    }

    private void addModel(Meta meta, Json json) {
//        val model = meta.model ?:return
//                json.writeObjectStart(Meta.JSON_MODEL)
//
//        // default materials
//
//        if (model.defaultMaterials != null) {
//            json.writeObjectStart(MetaModel.JSON_DEFAULT_MATERIALS)
//            for (mat in model.defaultMaterials) {
//                json.writeValue(mat.key, mat.value)
//            }
//            json.writeObjectEnd()
//        }
//
//        json.writeObjectEnd()
    }

    private void addTerrain(Meta meta, Json json) {
//        val terrain = meta.terrain ?:return
//
//                json.writeObjectStart(Meta.JSON_TERRAIN)
//        json.writeValue(MetaTerrain.JSON_SIZE, terrain.size)
//        json.writeValue(MetaTerrain.JSON_UV_SCALE, terrain.uv)
//        if (terrain.splatmap != null) json.writeValue(MetaTerrain.JSON_SPLATMAP, terrain.splatmap)
//        if (terrain.splatBase != null) json.writeValue(MetaTerrain.JSON_SPLAT_BASE, terrain.splatBase)
//        if (terrain.splatR != null) json.writeValue(MetaTerrain.JSON_SPLAT_R, terrain.splatR)
//        if (terrain.splatG != null) json.writeValue(MetaTerrain.JSON_SPLAT_G, terrain.splatG)
//        if (terrain.splatB != null) json.writeValue(MetaTerrain.JSON_SPLAT_B, terrain.splatB)
//        if (terrain.splatA != null) json.writeValue(MetaTerrain.JSON_SPLAT_A, terrain.splatA)
//        json.writeObjectEnd()
    }


}
