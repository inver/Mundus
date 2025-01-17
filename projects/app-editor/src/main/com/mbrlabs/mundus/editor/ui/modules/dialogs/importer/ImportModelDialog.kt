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

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage
import com.mbrlabs.mundus.editor.ui.AppUi
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget
import org.springframework.stereotype.Component

/**
 * @author Marcus Brummer
 * @version 07-06-2016
 */
@Component
class ImportModelDialog(
    appUi: AppUi,
    importModelPresenter: ImportModelPresenter,
    shaderStorage: ShaderStorage
) : BaseDialog("Import Mesh") {

    private val importWidget = ImportModelWidget(
        RenderWidget(appUi),
        importModelPresenter,
        shaderStorage
    ) { close() }

    init {
        isModal = true
        isMovable = true

        val root = VisTable()
        add<Table>(root).expand().fill()
        root.add(importWidget).minWidth(600f).expand().fill().left().top()
    }
}
