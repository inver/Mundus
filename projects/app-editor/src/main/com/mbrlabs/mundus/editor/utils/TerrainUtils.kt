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

@file:JvmName("TerrainUtils")

package com.mbrlabs.mundus.editor.utils

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset

private var tempVI = VertexInfo()

//fun createTerrainGO(
//    shader: BaseShader,
//    goID: Int,
//    goName: String,
//    terrain: TerrainAsset,
//    pickShader: BaseShader
//): GameObject {
//    val terrainGO = GameObject(null as String, goID)
//    terrainGO.name = goName
//
//    terrain.terrain.setTransform(terrainGO.transform)
//    val terrainComponent = PickableTerrainComponent(terrainGO, pickShader)
//    terrainComponent.terrain = terrain
//    terrainGO.components.add(terrainComponent)
//    terrainComponent.shader = shader
//    terrainComponent.encodeRayPickColorId()
//
//    return terrainGO
//}

fun getRayIntersection(terrains: Array<TerrainAsset>, ray: Ray, out: Vector3): Vector3? {
    for (terrain in terrains) {
//        val terr = terrain.terrain
//        terr.getRayIntersection(out, ray)
//        if (terr.isOnTerrain(out.x, out.z)) {
//            return out
//        }
    }
    return null
}

fun getRayIntersectionAndUp(terrains: Array<TerrainAsset>, ray: Ray): VertexInfo? {
    for (terrain in terrains) {
//        val terr = terrain.terrain
//        terr.getRayIntersection(tempVI.position, ray)
//        if (terr.isOnTerrain(tempVI.position.x, tempVI.position.z)) {
//            tempVI.normal.set(terr.getNormalAtWordCoordinate(tempVI.position.x, tempVI.position.z))
//            return tempVI
//        }
    }
    return null
}

