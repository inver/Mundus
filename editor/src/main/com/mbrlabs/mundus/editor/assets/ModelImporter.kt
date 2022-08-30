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

package com.mbrlabs.mundus.editor.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.UBJsonReader
import com.mbrlabs.mundus.commons.ac3d.Ac3dModelLoader
import com.mbrlabs.mundus.commons.ac3d.Ac3dParser
import com.mbrlabs.mundus.commons.core.AppModelLoader
import com.mbrlabs.mundus.commons.g3d.MG3dModelLoader
import com.mbrlabs.mundus.commons.gltf.GltfLoaderWrapper
import com.mbrlabs.mundus.commons.loader.obj.ObjLoaderWrapper
import com.mbrlabs.mundus.commons.utils.FileFormatUtils.FORMAT_3D_OBJ
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent
import com.mbrlabs.mundus.editor.utils.*
import org.apache.commons.io.FilenameUtils

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
class ModelImporter(private val registry: Registry) : SettingsChangedEvent.SettingsChangedListener {

    private val fbxConv: FbxConv
    private val loaders: Map<String, AppModelLoader>

    init {
        Mundus.registerEventListener(this)
        this.fbxConv = FbxConv(registry.settings.fbxConvBinary)
        this.loaders = mapOf(
            MG3dModelLoader.MODEL_TYPE to MG3dModelLoader(UBJsonReader()),
            GltfLoaderWrapper.MODEL_TYPE to GltfLoaderWrapper(Json()),
            Ac3dModelLoader.MODEL_TYPE to Ac3dModelLoader(Ac3dParser()),
            FORMAT_3D_OBJ to ObjLoaderWrapper()
        )
    }

    override fun onSettingsChanged(event: SettingsChangedEvent) {
        fbxConv.setFbxBinary(event.settings.fbxConvBinary)
    }

    fun importToTempFolder(modelFile: FileHandle?): FileHandleWithDependencies? {
        if (modelFile == null || !modelFile.exists()) {
            return null
        }

        val modelFileWithDependencies = loader(modelFile).getFileWithDependencies(modelFile)

        val tempModelCache = registry.createTempFolder()

        // copy model file
        modelFileWithDependencies.copyTo(tempModelCache)
        val rawModelFile = Gdx.files.absolute(FilenameUtils.concat(tempModelCache.path(), modelFile.name()))
        if (!rawModelFile.exists()) {
            return null
        }

        var retFile: FileHandleWithDependencies? = null
        // convert copied importer
        val convert = isFBX(rawModelFile) || isCollada(rawModelFile) /*|| isWavefont(rawModelFile)*/
        if (convert) {
            fbxConv.clear()
            val convResult = fbxConv
                .input(rawModelFile.path())
                .output(tempModelCache.file().absolutePath)
                .flipTexture(true)
                .execute()

            if (convResult.isSuccess) {
                retFile = FileHandleWithDependencies(Gdx.files.absolute(convResult.outputFile))
            }
        } else if (isG3DB(rawModelFile) || isGLTF(rawModelFile)) {
            retFile = FileHandleWithDependencies(rawModelFile)
        } else if (isAC3D(rawModelFile)) {
            retFile = FileHandleWithDependencies(rawModelFile)
        } else if (isOBJ(rawModelFile)) {
            retFile = FileHandleWithDependencies(rawModelFile)
        }

        // check if converted file exists
        return if (retFile != null && retFile.exists()) retFile else null;
    }

    private fun loader(file: FileHandle): AppModelLoader {
        if (isG3DB(file)) {
            return loaders[MG3dModelLoader.MODEL_TYPE]!!
        }
        if (isGLTF(file)) {
            return loaders[GltfLoaderWrapper.MODEL_TYPE]!!
        }
        if (isAC3D(file)) {
            return loaders[Ac3dModelLoader.MODEL_TYPE]!!
        }
        if (isOBJ(file)) {
            return loaders[FORMAT_3D_OBJ]!!
        }
        throw GdxRuntimeException("Unsupported 3D format")
    }

    fun loadModel(file: FileHandle): Model? {
        return loader(file).loadModel(file)
    }

}
