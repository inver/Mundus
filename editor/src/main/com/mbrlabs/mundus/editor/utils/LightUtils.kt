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

@file:JvmName("LightUtils")

package com.mbrlabs.mundus.editor.utils

import com.mbrlabs.mundus.commons.env.lights.DirectionalLight
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.SceneGraph
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.DirectionalLightComponent

fun createDirectionalLightGO(sg: SceneGraph, goID: Int, goName: String): GameObject {
    val lightGO = GameObject(sg, null, goID)
    lightGO.name = goName

    // TODO: make pickable light component
    val lightComponent = DirectionalLightComponent(lightGO, DirectionalLight())
    lightGO.components.add(lightComponent)

    return lightGO
}