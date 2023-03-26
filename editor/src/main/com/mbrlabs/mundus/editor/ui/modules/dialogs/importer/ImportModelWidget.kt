package com.mbrlabs.mundus.editor.ui.modules.dialogs.importer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.model.ImportedModel
import com.mbrlabs.mundus.editor.ui.widgets.FileChooserField
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget
import com.mbrlabs.mundus.editor.ui.widgets.presenter.FileChooserFieldPresenter
import net.nevinsky.mundus.core.shader.DefaultShaderProvider
import net.nevinsky.mundus.core.ModelBatch
import net.nevinsky.mundus.core.ModelInstance

class ImportModelWidget(
    private val renderWidget: RenderWidget,
    private val importModelPresenter: ImportModelPresenter,
    fileChooserFieldPresenter: FileChooserFieldPresenter,
    closeListener: Runnable
) : VisTable(), Disposable {

    private val modelInput = FileChooserField(300)

    private val env = Environment()
    private val cam = PerspectiveCamera()

    val importBtn = VisTextButton("Import")

    var importedModel: ImportedModel? = null
    private var modelBatch: ModelBatch? = null
    var previewInstance: ModelInstance? = null

    init {
        cam.position.set(0f, 5f, 5f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 0.1f
        cam.far = 100f
        cam.update()

        env.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        env.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))


        renderWidget.setCam(cam)
        renderWidget.setRenderer { camera ->
            if (previewInstance == null) {
                return@setRenderer
            }

            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
            previewInstance!!.transform.rotate(0f, 1f, 0f, -1f)

            modelBatch?.begin(camera)
            modelBatch?.render(previewInstance!!, env)
            modelBatch?.end()
        }

        setupUI()
        importModelPresenter.initImportButton(this, closeListener)
        fileChooserFieldPresenter.initFileChooserField(modelInput)
    }

    private fun setupUI() {
        val root = Table()
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f)
        add(root)

        val inputTable = VisTable()
        root.add(inputTable).width(300f).height(300f).padRight(10f)
        root.add(renderWidget).width(300f).height(300f).expand().fill()

        inputTable.left().top()
        inputTable.add(VisLabel("Model File")).left().padBottom(5f).row()
        inputTable.add(modelInput).fillX().expandX().padBottom(10f).row()
        inputTable.add(importBtn).fillX().expand().bottom()

        modelInput.setEditable(false)
        modelInput.setCallback { fileHandle ->
            if (fileHandle.exists()) {
                importedModel = importModelPresenter.importModelFromFile(fileHandle)
                if (importedModel == null) {
                    Dialogs.showErrorDialog(stage, "Import error\nPlease make sure you specified the right files")
                    return@setCallback
                }
                previewInstance = ModelInstance(importedModel?.model)
                showPreview()
            }
        }
    }

    private fun showPreview() {
        val config = DefaultShader.Config()
        config.numBones = 600 // TODO get max bones from model
        modelBatch = ModelBatch(DefaultShaderProvider())

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
        if (previewInstance != null) {
            previewInstance = null
        }
        modelBatch?.dispose()
        modelInput.clear()
    }
}