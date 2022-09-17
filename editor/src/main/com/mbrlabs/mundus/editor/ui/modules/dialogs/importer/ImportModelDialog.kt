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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.importer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.exceptions.AssetAlreadyExistsException
import com.mbrlabs.mundus.commons.assets.meta.dto.MetaModel
import com.mbrlabs.mundus.commons.assets.model.ModelAsset
import com.mbrlabs.mundus.commons.core.ModelFiles
import com.mbrlabs.mundus.commons.loader.ModelImporter
import com.mbrlabs.mundus.editor.assets.MetaSaver
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.AssetImportEvent
import com.mbrlabs.mundus.editor.events.EventBus
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget
import com.mbrlabs.mundus.editor.ui.widgets.presenter.FileChooserFieldPresenter
import com.mbrlabs.mundus.editor.utils.Log
import com.mbrlabs.mundus.editor.utils.Toaster
import com.mbrlabs.mundus.editor.utils.isCollada
import com.mbrlabs.mundus.editor.utils.isFBX
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
@Component
class ImportModelDialog(
    private val appUi: AppUi,
    private val toaster: Toaster,
    private val modelImporter: ModelImporter,
    private val assetManager: EditorAssetManager,
    private val eventBus: EventBus,
    private val fileChooserFieldPresenter: FileChooserFieldPresenter,
    private val registry: Registry
) : BaseDialog("Import Mesh"), Disposable {

    companion object {
        private val TAG = ImportModelDialog::class.java.simpleName
    }

    private val importMeshTable: ImportModelTable

    init {
        isModal = true
        isMovable = true

        val root = VisTable()
        add<Table>(root).expand().fill()
        importMeshTable = ImportModelTable()
        fileChooserFieldPresenter.initFileChooserField(importMeshTable.modelInput)
        root.add(importMeshTable).minWidth(600f).expand().fill().left().top()
    }

    override fun dispose() {
        importMeshTable.dispose()
    }

    /**
     */
    private inner class ImportModelTable : VisTable(), Disposable {
        // UI elements
        private var renderWidget: RenderWidget? = null
        private val importBtn = VisTextButton("IMPORT")
        val modelInput = FileChooserField(300)

        // preview model + instance
        private var previewModel: Model? = null
        private var previewInstance: ModelInstance? = null

        private var importedModel: ModelFiles? = null

        private var modelBatch: ModelBatch? = null
        private val cam: PerspectiveCamera = PerspectiveCamera()
        private val env: Environment

        init {

            cam.position.set(0f, 5f, 5f)
            cam.lookAt(0f, 0f, 0f)
            cam.near = 0.1f
            cam.far = 100f
            cam.update()

            env = Environment()
            env.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
            env.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))

            this.setupUI()
            this.setupListener()
        }

        private fun setupUI() {
            val root = Table()
            // root.debugAll();
            root.padTop(6f).padRight(6f).padBottom(22f)
            add(root)

            val inputTable = VisTable()
            renderWidget = RenderWidget(appUi, cam)
            renderWidget!!.setRenderer { camera ->
                if (previewInstance != null) {
                    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
                    previewInstance!!.transform.rotate(0f, 1f, 0f, -1f)
                    modelBatch?.begin(camera)
                    modelBatch?.render(previewInstance!!, env)
                    modelBatch?.end()
                }
            }

            root.add(inputTable).width(300f).height(300f).padRight(10f)
            root.add<RenderWidget>(renderWidget).width(300f).height(300f).expand().fill()

            inputTable.left().top()
            inputTable.add(VisLabel("Model File")).left().padBottom(5f).row()
            inputTable.add(modelInput).fillX().expandX().padBottom(10f).row()
            inputTable.add(importBtn).fillX().expand().bottom()

            modelInput.setEditable(false)
        }

        private fun setupListener() {

            // model chooser
            modelInput.setCallback { fileHandle ->
                if (fileHandle.exists()) {
                    loadAndShowPreview(fileHandle)
                }
            }

            // import btn
            importBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (previewModel != null && previewInstance != null) {
                        try {
                            val modelAsset = importModel()
                            eventBus.post(AssetImportEvent(modelAsset))
                            toaster.success("Mesh imported")
                        } catch (e: IOException) {
                            e.printStackTrace()
                            toaster.error("Error while creating a ModelAsset")
                        } catch (ee: AssetAlreadyExistsException) {
                            Log.exception(TAG, ee)
                            toaster.error("Error: There already exists a model with the same name")
                        }

                        dispose()
                        close()
                    } else {
                        toaster.error("There is nothing to import")
                    }
                }
            })
        }

        @Throws(IOException::class, AssetAlreadyExistsException::class)
        private fun importModel(): ModelAsset {

            // create model asset
            val modelAsset = assetManager.createModelAsset(importedModel!!)

            // create materials
            modelAsset.meta.model = MetaModel()
            for (mat in modelAsset.model.materials) {
                val materialAsset = assetManager.createMaterialAsset(modelAsset.id.substring(0, 4) + "_" + mat.id)
                modelAsset.meta.model.defaultMaterials.put(mat.id, materialAsset.id)
                modelAsset.defaultMaterials.put(mat.id, materialAsset)
            }

            // save meta file
            val saver = MetaSaver()
            saver.save(modelAsset.meta)

            modelAsset.applyDependencies()

            return modelAsset
        }

        private fun loadAndShowPreview(model: FileHandle) {
            val tmpFolder = registry.createTempFolder()
            importedModel = modelImporter.importToTempFolder(tmpFolder, model)

            if (importedModel == null) {
                if (isCollada(model) || isFBX(model)) {
                    Dialogs.showErrorDialog(
                        stage, "Import error\nPlease make sure you specified the right "
                                + "files & have set the correct fbc-conv binary in the settings menu."
                    )
                } else {
                    Dialogs.showErrorDialog(stage, "Import error\nPlease make sure you specified the right files")
                }
            }

            // load and show preview
            if (importedModel != null) {
                try {
                    previewModel = modelImporter.loadModel(importedModel!!.main)

                    previewInstance = ModelInstance(previewModel!!)
                    showPreview()
                } catch (e: GdxRuntimeException) {
                    Dialogs.showErrorDialog(stage, e.message)
                }

            }
        }

        private fun showPreview() {
            previewInstance = ModelInstance(previewModel!!)

            val config = DefaultShader.Config()
            config.numBones = 60 // TODO get max bones from model
            modelBatch = ModelBatch(DefaultShaderProvider(config))

            // scale to 2 open gl units
            val boundingBox = previewInstance!!.calculateBoundingBox(BoundingBox())
            val max = boundingBox.getMax(Vector3())
            var maxDim = 0f
            if (max.x > maxDim) maxDim = max.x
            if (max.y > maxDim) maxDim = max.y
            if (max.z > maxDim) maxDim = max.z
            previewInstance!!.transform.scl(2f / maxDim)
        }

        override fun dispose() {
            if (previewModel != null) {
                previewModel!!.dispose()
                previewModel = null
                previewInstance = null
            }
            modelBatch?.dispose()
            modelInput.clear()
        }
    }

}
