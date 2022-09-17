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

import com.mbrlabs.mundus.editor.core.registry.Registry
import com.mbrlabs.mundus.editor.events.SettingsChangedEvent

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
@Deprecated("Use Model importer from common module")
class ModelImporterOld(registry: Registry) : SettingsChangedEvent.SettingsChangedListener {

    private val fbxConv: FbxConv

    init {
//        eventBus.register(this)
        fbxConv = FbxConv(registry.getSettings().fbxConvBinary)
    }

    override fun onSettingsChanged(event: SettingsChangedEvent) {
        fbxConv.setFbxBinary(event.settings.fbxConvBinary)
    }
}
