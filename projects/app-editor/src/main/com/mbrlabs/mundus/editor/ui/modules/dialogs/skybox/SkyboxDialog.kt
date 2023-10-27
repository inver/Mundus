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

package com.mbrlabs.mundus.editor.ui.modules.dialogs.skybox

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.assets.meta.Meta
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAsset
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxMeta
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.chooser.image.ImageChooserField
import com.mbrlabs.mundus.editor.ui.widgets.TextFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.chooser.file.FileChooserFieldPresenter
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 10-01-2016
 */
@Component
class SkyboxDialog(
    imageChooserFieldPresenter: FileChooserFieldPresenter,
    private val assetManager: EditorAssetManager
) : BaseDialog("New Skybox") {

    val nameField = TextFieldWithLabel("Name")

    private val root = VisTable()

    val right =
        ImageChooserField(100, "Right (+X)")
    val left = ImageChooserField(100, "Left (-X)")
    val top = ImageChooserField(100, "Top (+Y)")
    val bottom =
        ImageChooserField(100, "Bottom (-Y)")
    val back = ImageChooserField(100, "Back (+Z)")
    val front =
        ImageChooserField(100, "Front (-Z)")

    val saveBtn = VisTextButton("Create")

    init {
//        debugAll()
        imageChooserFieldPresenter.initImageChooserField(right)
        imageChooserFieldPresenter.initImageChooserField(left)
        imageChooserFieldPresenter.initImageChooserField(top)
        imageChooserFieldPresenter.initImageChooserField(bottom)
        imageChooserFieldPresenter.initImageChooserField(back)
        imageChooserFieldPresenter.initImageChooserField(front)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        contentTable.add(nameField).top().left().grow().fillX().padTop(4f).padBottom(4f).row()
        contentTable.add(root).left().top()
        root.padRight(6f).left().top()
        root.add(VisLabel("The 6 images must be square and of equal size")).colspan(4).row()
        root.addSeparator().colspan(4).row()
        root.add(top).padLeft(108f).colspan(2).row()
        root.add(left)
        root.add(front)
        root.add(right)
        root.add(back).row()
        root.add(bottom).padLeft(108f).colspan(2).row()
        root.addSeparator().colspan(4).row()
        root.add(saveBtn).top().right().padTop(8f).padBottom(8f).colspan(4).row()
    }

    private fun setupListeners() {
        // create btn
        saveBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                val scene = ctx.current.currentScene;
//                val oldSkybox = scene.skyboxName
//                oldSkybox?.dispose()

//                scene.skybox = Skybox(
//                    positiveX.file, negativeX.file,
//                    positiveY.file, negativeY.file, positiveZ.file, negativeZ.file
//                )
//                resetImages()
            }
        })
    }

    fun init(skyboxName: String?) {
        val isNew = StringUtils.isEmpty(skyboxName)
        changeDialogElements(isNew)
        if (isNew) {
            right.setImage(null)
            left.setImage(null)
            top.setImage(null)
            bottom.setImage(null)
            back.setImage(null)
            front.setImage(null)
            return
        }

        nameField.text = skyboxName

        //todo move this to presenter
        val meta = (assetManager.loadCurrentProjectAsset(skyboxName) as SkyboxAsset).meta as Meta<SkyboxMeta>

        right.setImage(meta.file.child(meta.additional.right))
        left.setImage(meta.file.child(meta.additional.left))
        top.setImage(meta.file.child(meta.additional.top))
        bottom.setImage(meta.file.child(meta.additional.bottom))
        back.setImage(meta.file.child(meta.additional.back))
        front.setImage(meta.file.child(meta.additional.front))
    }

    private fun changeDialogElements(isCreate: Boolean) {
        nameField.setEditable(isCreate)
        if (isCreate) {
            titleLabel.setText("New Skybox")
            saveBtn.setText("Create")
        } else {
            titleLabel.setText("Edit Skybox")
            saveBtn.setText("Save")
        }
    }

}
