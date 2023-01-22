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

import com.badlogic.gdx.files.FileHandle
import com.mbrlabs.mundus.commons.assets.AssetType
import com.mbrlabs.mundus.commons.assets.OldAssetManager
import com.mbrlabs.mundus.commons.assets.exceptions.AssetAlreadyExistsException
import com.mbrlabs.mundus.commons.assets.meta.Meta
import com.mbrlabs.mundus.commons.assets.model.ModelAsset
import com.mbrlabs.mundus.commons.model.ImportedModel
import org.apache.commons.io.FilenameUtils
import org.slf4j.LoggerFactory
import java.io.*
import java.util.*

/**
 * @author Marcus Brummer
 * @version 24-01-2016
 */
class OldEditorAssetManager(assetsRoot: FileHandle) : OldAssetManager(assetsRoot) {

    companion object {
        private val log = LoggerFactory.getLogger(OldEditorAssetManager::class.java)
        val STANDARD_ASSET_TEXTURE_CHESSBOARD = "chessboard"
    }

    /** Modified assets that need to be saved.  */
//    private val dirtyAssets = ObjectSet<Asset>()
//    private val metaSaver = Me()

    init {
        if (rootFolder != null && (!rootFolder.exists() || !rootFolder.isDirectory)) {
            log.error( "Root asset folder is not a directory")
        }
    }

//    fun addDirtyAsset(asset: Asset) {
//        dirtyAssets.add(asset)
//    }
//
//    fun getDirtyAssets(): ObjectSet<Asset> {
//        return dirtyAssets
//    }

    /**
     * Creates a new meta file and saves it at the given location.
     *
     * @param file
     *            save location
     * @param type
     *            asset type
     * @return saved meta file
     *
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createNewMetaFile(file: FileHandle, type: AssetType): Meta<Any>? {
        if (file.exists()) {
//            eventBus.post(
//                LogEvent(
//                    LogType.ERROR,
//                    "Tried to create new Meta File that already exists: " + file.name()
//                )
//            )
            throw AssetAlreadyExistsException()
        }

//        val meta = Meta(file)
//        meta.uuid = newUUID()
//        meta.version = Meta.CURRENT_VERSION
//        meta.lastModified = System.currentTimeMillis()
//        meta.type = type
//        metaSaver.save(meta)

        return null
    }

    private fun newUUID(): String {
        return UUID.randomUUID().toString().replace("-".toRegex(), "")
    }

    /**
     * Creates a couple of standard assets.
     *
     * Creates a couple of standard assets in the current project, that should
     * be included in every project.
     */
//    fun createStandardAssets() {
//        try {
//            // chessboard
//            val chessboard = createTextureAsset(Gdx.files.internal("bundled/chessboard.png"))
//            assetIndex.remove(chessboard.id)
//            chessboard.meta.uuid = STANDARD_ASSET_TEXTURE_CHESSBOARD
//            assetIndex.put(chessboard.id, chessboard)
////            metaSaver.save(chessboard.meta)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

    /**
     * Creates a new model asset.
     *
     * Creates a new model asset in the current project and adds it to this
     * asset manager.
     *
     * @param model imported model
     * @return model asset
     *
     * @throws IOException
     */
    @Throws(IOException::class, AssetAlreadyExistsException::class)
    fun createModelAsset(model: ImportedModel): ModelAsset? {
        val modelFilename = model.name()
        val metaFilename = "$modelFilename.meta"

        // create meta file
        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.MODEL)

        // copy model file
//        model.copyTo(FileHandle(rootFolder.path()))

        // load & return asset
        val assetFile = FileHandle(FilenameUtils.concat(rootFolder.path(), modelFilename))
//        val asset = ModelAsset(meta, assetFile)
//        asset.load()

//        addAsset(asset)
//        return asset
        return null
    }

    /**
     * Creates a new terrainAsset asset.
     *
     * This creates a .terra file (height data) and a pixmap texture (splatmap).
     * The asset will be added to this asset manager.
     *
     * @param vertexResolution
     *            vertex resolution of the terrainAsset
     * @param size
     *            terrainAsset size
     * @return new terrainAsset asset
     * @throws IOException
     */
//    @Throws(IOException::class, AssetAlreadyExistsException::class)
//    fun createTerraAsset(name: String, vertexResolution: Int, size: Int): TerrainAsset {
//        val terraFilename = "$name.terra"
//        val metaFilename = "$terraFilename.meta"
//
//        // create meta file
//        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
//        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.TERRAIN)
//        meta.terrain = MetaTerrain()
//        meta.terrain.size = size
//        meta.terrain.uv = 60f
//        metaSaver.save(meta)
//
//        // create terra file
//        val terraPath = FilenameUtils.concat(rootFolder.path(), terraFilename)
//        val terraFile = File(terraPath)
//        FileUtils.touch(terraFile)
//
//        // create initial height data
//        val data = FloatArray(vertexResolution * vertexResolution)
//        for (i in data.indices) {
//            data[i] = 0f
//        }
//
//        // write terra file
//        val outputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(terraFile)))
//        for (f in data) {
//            outputStream.writeFloat(f)
//        }
//        outputStream.flush()
//        outputStream.close()
//
//        // load & apply standard chessboard texture
//        val asset =
//            TerrainAsset(meta, FileHandle(terraFile))
//        asset.load()
//
//        // set base texture
//        val chessboard = findAssetByID(STANDARD_ASSET_TEXTURE_CHESSBOARD)
//        if (chessboard != null) {
//            asset.splatBase = chessboard as TextureAsset
//            asset.applyDependencies()
//            metaSaver.save(asset.meta)
//        }
//
//        addAsset(asset)
//        return asset
//    }

    /**
     * Creates a new pixmap texture asset.
     *
     * This creates a new pixmap texture and adds it to this asset manager.
     *
     * @param size
     *            size of the pixmap in pixels
     * @return new pixmap asset
     * @throws IOException
     */
//    @Throws(IOException::class, AssetAlreadyExistsException::class)
//    fun createPixmapTextureAsset(size: Int): PixmapTextureAsset {
//        val pixmapFilename = newUUID().substring(0, 5) + ".png"
//        val metaFilename = pixmapFilename + ".meta"
//
//        // create meta file
//        val metaPath = FilenameUtils.concat(rootFolder.path(), metaFilename)
//        val meta = createNewMetaFile(FileHandle(metaPath), AssetType.PIXMAP_TEXTURE)
//
//        // create pixmap
//        val pixmapPath = FilenameUtils.concat(rootFolder.path(), pixmapFilename)
//        val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
//        val pixmapAssetFile = FileHandle(pixmapPath)
//        PixmapIO.writePNG(pixmapAssetFile, pixmap)
//        pixmap.dispose()
//
//        // load & return asset
//        val asset = PixmapTextureAsset(meta, pixmapAssetFile)
//        asset.load()
//
//        addAsset(asset)
//        return asset
//    }

    /**
     * Creates a new texture asset using the given texture file.
     *
     * @param texture
     * @return
     * @throws IOException
     */
//    @Throws(IOException::class, AssetAlreadyExistsException::class)
//    fun createTextureAsset(texture: FileHandle): TextureAsset {
//        val meta = createMetaFileFromAsset(texture, AssetType.TEXTURE)
//        val importedAssetFile = copyToAssetFolder(texture)
//
//        val asset = TextureAsset(meta, importedAssetFile)
//        // TODO parse special texture instead of always setting them
//        asset.setTileable(true)
//        asset.generateMipmaps(true)
//        asset.load()
//
//        addAsset(asset)
//        return asset
//    }

    /**
     * Creates a new & empty material asset.
     *
     * @return new material asset
     * @throws IOException
     */
//    @Throws(IOException::class, AssetAlreadyExistsException::class)
//    fun createMaterialAsset(name: String): MaterialAsset {
//        // create empty material file
//        val path = FilenameUtils.concat(rootFolder.path(), name) + MaterialAsset.EXTENSION
//        val matFile = Gdx.files.absolute(path)
//        FileUtils.touch(matFile.file())
//
//        val meta = createMetaFileFromAsset(matFile, AssetType.MATERIAL)
//        val asset = MaterialAsset(meta, matFile)
//        asset.load()
//
//        saveAsset(asset)
//        addAsset(asset)
//        return asset
//    }

    /**
     * @param asset
     * @throws IOException
     */
//    @Throws(IOException::class)
//    fun saveAsset(asset: Asset) {
//        if (asset is MaterialAsset) {
//            saveMaterialAsset(asset)
//        } else if (asset is TerrainAsset) {
//            saveTerrainAsset(asset)
//        } else if (asset is ModelAsset) {
//            saveModelAsset(asset)
//        }
//        // TODO other assets ?
//    }

    /**
     * @param asset
     */
//    @Throws(IOException::class)
//    fun saveModelAsset(asset: ModelAsset) {
//        for (g3dbMatID in asset.defaultMaterials.keys) {
//            asset.meta.model.defaultMaterials.put(g3dbMatID, asset.defaultMaterials[g3dbMatID]!!.id)
//        }
//        metaSaver.save(asset.meta)
//    }
//
//    /**
//     * Delete the asset from the project
//     */
//    fun deleteAsset(asset: Asset, projectManager: ProjectManager) {
//        val objectsUsingAsset = findAssetUsagesInScenes(projectManager, asset)
//        val assetsUsingAsset = findAssetUsagesInAssets(asset)
//
//        if (objectsUsingAsset.isNotEmpty() || assetsUsingAsset.isNotEmpty()) {
//            showUsagesFoundDialog(objectsUsingAsset, assetsUsingAsset)
//            return
//        }
//
//        // continue with deletion
//        assets?.removeValue(asset, true)
//
//        if (asset.file.extension().equals(FileFormatUtils.FORMAT_3D_GLTF)) {
//            // Delete the additional gltf binary file if found
//            val binPath = asset.file.pathWithoutExtension() + ".bin"
//            val binFile = Gdx.files.getFileHandle(binPath, Files.FileType.Absolute)
//            if (binFile.exists())
//                binFile.delete()
//        }
//
//        if (asset.meta.file.exists())
//            asset.meta.file.delete()
//
//        if (asset.file.exists())
//            asset.file.delete()
//    }
//
//    /**
//     * Build a dialog displaying the usages for the asset trying to be deleted.
//     */
//    private fun showUsagesFoundDialog(
//        objectsWithAssets: HashMap<GameObject, String>,
//        assetsUsingAsset: ArrayList<Asset>
//    ) {
//        val iterator = objectsWithAssets.iterator()
//        var details = "Scenes using asset:"
//
//        // Create scenes section
//        while (iterator.hasNext()) {
//            val next = iterator.next()
//
//            val sceneName = next.value
//            val gameObject = next.key
//
//            var moreDetails = buildString {
//                append("\nScene: ")
//                append("[")
//                append(sceneName)
//                append("] Object name: ")
//                append("[")
//                append(gameObject.name)
//                append("]")
//            }
//
//            if (iterator.hasNext()) {
//                moreDetails += ", "
//            }
//
//            details += (moreDetails)
//        }
//
//        // add assets section
//        if (assetsUsingAsset.isNotEmpty()) {
//            details += "\n\nAssets using asset:"
//
//            for (name in assetsUsingAsset)
//                details += "\n" + name
//        }

//        Dialogs.showDetailsDialog(
//            UI,
//            "Before deleting an asset, remove usages of the asset and save. See details for usages.",
//            "Asset deletion",
//            details
//        )
//    }

    /**
     * Searches all assets in the current context for any usages of the given asset
     */
//    private fun findAssetUsagesInAssets(asset: Asset): ArrayList<Asset> {
//        val assetsUsingAsset = ArrayList<Asset>()
//
//        // Check for dependent assets that are not in scenes
//        for (otherAsset in assets) {
//            if (asset != otherAsset && otherAsset.usesAsset(asset)) {
//                assetsUsingAsset.add(otherAsset)
//            }
//        }
//
//        return assetsUsingAsset
//    }
//
//    /**
//     * Searches all scenes in the current context for any usages of the given asset
//     */
//    private fun findAssetUsagesInScenes(projectManager: ProjectManager, asset: Asset): HashMap<GameObject, String> {
//        val objectsWithAssets = HashMap<GameObject, String>()
//
//        // we check for usages in all scenes
////        for (sceneName in projectManager.current.scenes) {
////            val scene = projectManager.loadScene(projectManager.current, sceneName)
////            checkSceneForAssetUsage(scene, asset, objectsWithAssets)
////        }
//
//        return objectsWithAssets
//    }
//
////    private fun checkSceneForAssetUsage(
////        scene: EditorScene?,
////        asset: Asset,
////        objectsWithAssets: HashMap<GameObject, String>
////    ) {
////        for (gameObject in scene!!.sceneGraph.gameObjects) {
////            for (component in gameObject.components) {
////                if (component is AssetUsage) {
////                    if (component.usesAsset(asset))
////                        objectsWithAssets[gameObject] = scene.name
////                }
////            }
////        }
////    }
//
//    /**
//     * Saves an existing terrainAsset asset.
//     *
//     * This updates all modifiable assets and the meta file.
//     *
//     * @param terrain
//     *             terrainAsset asset
//     * @throws IOException
//     */
//    @Throws(IOException::class)
//    fun saveTerrainAsset(terrain: TerrainAsset) {
//        // save .terra file
//        val outputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(terrain.file.file())))
//        for (f in terrain.terrain.heightData) {
//            outputStream.writeFloat(f)
//        }
//        outputStream.flush()
//        outputStream.close()
//
//        // save splatmap
//        val splatmap = terrain.splatmap
//        if (splatmap != null) {
//            PixmapIO.writePNG(splatmap.file, splatmap.pixmap)
//        }
//
//        // save meta file
//        metaSaver.save(terrain.meta)
//    }
//
//    @Throws(IOException::class)
//    fun saveMaterialAsset(mat: MaterialAsset) {
//        // save .mat
//        val props = Properties()
//        if (mat.diffuseColor != null) {
//            props.setProperty(MaterialAsset.PROP_DIFFUSE_COLOR, mat.diffuseColor.toString())
//        }
//        if (mat.diffuseTexture != null) {
//            props.setProperty(MaterialAsset.PROP_DIFFUSE_TEXTURE, mat.diffuseTexture.id)
//        }
//        if (mat.normalMap != null) {
//            props.setProperty(MaterialAsset.PROP_MAP_NORMAL, mat.normalMap.id)
//        }
//        props.setProperty(MaterialAsset.PROP_OPACITY, mat.opacity.toString())
//        props.setProperty(MaterialAsset.PROP_SHININESS, mat.shininess.toString())
//
//        val fileOutputStream = FileOutputStream(mat.file.file())
//        props.store(fileOutputStream, null)
//        fileOutputStream.flush()
//        fileOutputStream.close()
//
//        // save meta file
//        metaSaver.save(mat.meta)
//    }
//
//    @Throws(IOException::class, AssetAlreadyExistsException::class)
//    private fun createMetaFileFromAsset(assetFile: FileHandle, type: AssetType): Meta {
//        val metaName = assetFile.name() + "." + Meta.META_EXTENSION
//        val metaPath = FilenameUtils.concat(rootFolder.path(), metaName)
//        return createNewMetaFile(FileHandle(metaPath), type)
//    }
//
//    private fun copyToAssetFolder(file: FileHandle): FileHandle {
//        val copy = FileHandle(FilenameUtils.concat(rootFolder.path(), file.name()))
//        file.copyTo(copy)
//        return copy
//    }

}
