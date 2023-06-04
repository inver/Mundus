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

package com.mbrlabs.mundus.editor.exporter

import com.badlogic.gdx.files.FileHandle
import com.kotcrab.vis.ui.util.async.AsyncTaskListener
import com.mbrlabs.mundus.commons.assets.Asset
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.project.ProjectContext
import com.mbrlabs.mundus.editor.core.scene.SceneStorage
import org.apache.commons.io.FilenameUtils
import java.io.File

/**
 * @author Marcus Brummer
 * @version 26-10-2016
 */
class Exporter(
    val project: ProjectContext,
    val sceneStorage: SceneStorage,
    val assetManager: EditorAssetManager
) {
    fun exportAsync(outputFolder: FileHandle, listener: AsyncTaskListener) {
//
//        // convert current project on the main thread to avoid nested array iterators
//        // because it would iterate over the scene graph arrays while rendering (on the main thread)
//        // and while converting (on the other thread)
//        val currentSceneDTO = SceneConverter.convert(project.getCurrentScene())
//        val jsonType = project.settings.export.jsonType
//
//        val task = object : AsyncTask("export_${project.name}") {
//            override fun doInBackground() {
//                val step = 100f / (assetManager.assets.size)
//                var progress = 0f
//
//                // create folder structure
//                createFolders(outputFolder)
//
//                // copy assets
//                val assetFolder = FileHandle(FilenameUtils.concat(outputFolder.path(), "assets/"))
//                val scenesFolder = FileHandle(FilenameUtils.concat(outputFolder.path(), "scenes/"))
//
//                // sleep a bit to open the progress dialog
//                Thread.sleep(250)
//
//                for (asset in assetManager.assets) {
//                    exportAsset(asset, assetFolder)
//                    progress += step
//                    setProgressPercent(progress.toInt())
//                    setMessage(asset.id)
//                    Thread.sleep(50)
//                }
//
//                // load, convert & copy scenes
////                for (sceneName in project.scenes) {
////                    val file = FileHandle(
////                        FilenameUtils.concat(
////                            scenesFolder.path(),
////                            "$sceneName.$PROJECT_SCENE_EXTENSION"
////                        )
////                    )
////
////                    // load from disk or convert current scene
////                    var scene: SceneDto
////                    if (project.getCurrentScene().name == sceneName) {
////                        scene = currentSceneDTO
////                    } else {
////                        scene = sceneStorage.loadScene(project.path, sceneName)
////                    }
////
////                    // convert & export
////                    exportScene(scene, file, jsonType)
////                    progress += step
////                    setProgressPercent(progress.toInt())
////                    setMessage(scene.name)
////                    Thread.sleep(50)
////                }
//            }
//        }
//
//        task.addListener(listener)
//        task.execute()
    }

    private fun createFolders(exportRootFolder: FileHandle) {
        // ROOT/assets
        val assets = File(FilenameUtils.concat(exportRootFolder.path(), "assets/"))
        assets.mkdirs()

        // ROOT/scenes
        val scenes = File(FilenameUtils.concat(exportRootFolder.path(), "scenes/"))
        scenes.mkdirs()
    }

    private fun exportAsset(asset: Asset<Any>, folder: FileHandle) {
//        asset.file.copyTo(folder)
//        asset.meta.file.copyTo(folder)
    }


}
