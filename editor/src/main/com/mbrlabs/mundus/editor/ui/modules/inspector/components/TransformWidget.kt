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

package com.mbrlabs.mundus.editor.ui.modules.inspector.components

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.kotcrab.vis.ui.widget.Separator.SeparatorStyle
import com.kotcrab.vis.ui.widget.VisLabel
import com.mbrlabs.mundus.commons.core.ecs.component.PositionComponent
import com.mbrlabs.mundus.editor.core.project.EditorCtx
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.utils.formatFloat

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
class TransformWidget(
    separator: SeparatorStyle?,
    private val ctx: EditorCtx
) : BaseInspectorWidget(separator, "Transformation") {

    companion object {
        private val tempV3 = Vector3()
        private val tempQuat = Quaternion()
    }

    private val FIELD_SIZE = 65
    private val posX = FloatFieldWithLabel("x", FIELD_SIZE)
    private val posY = FloatFieldWithLabel("y", FIELD_SIZE)
    private val posZ = FloatFieldWithLabel("z", FIELD_SIZE)

    private val rotX = FloatFieldWithLabel("x", FIELD_SIZE)
    private val rotY = FloatFieldWithLabel("y", FIELD_SIZE)
    private val rotZ = FloatFieldWithLabel("z", FIELD_SIZE)

    private val scaleX = FloatFieldWithLabel("x", FIELD_SIZE)
    private val scaleY = FloatFieldWithLabel("y", FIELD_SIZE)
    private val scaleZ = FloatFieldWithLabel("z", FIELD_SIZE)

    private val lookAt = FloatFieldWithLabel("Look at entity chooser here", FIELD_SIZE)

    init {
        isDeletable = false
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val pad = 4
        collapsibleContent.add(VisLabel("Position: ")).padRight(5f).padBottom(pad.toFloat()).left()
        collapsibleContent.add(posX).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add(posY).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add(posZ).padBottom(pad.toFloat()).row()

        collapsibleContent.add(VisLabel("Rotation: ")).padRight(5f).padBottom(pad.toFloat()).left()
        collapsibleContent.add(rotX).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add(rotY).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add(rotZ).padBottom(pad.toFloat()).row()

        collapsibleContent.add(VisLabel("Scale: ")).padRight(5f).padBottom(pad.toFloat()).left()
        collapsibleContent.add(scaleX).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add(scaleY).padBottom(pad.toFloat()).padRight(pad.toFloat())
        collapsibleContent.add(scaleZ).padBottom(pad.toFloat()).row()

        collapsibleContent.add(VisLabel("Look at: ")).padRight(5f).padBottom(pad.toFloat()).left()
        collapsibleContent.add(lookAt).padBottom(pad.toFloat()).padRight(pad.toFloat()).row
    }

    private fun setupListeners() {

        // position
        posX.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (ctx.selectedEntityId < 0) {
                    return
                }
                val position = ctx.currentWorld.getEntity(ctx.selectedEntityId)
                    .getComponent(PositionComponent::class.java)

                val pos = position.getLocalPosition(tempV3)
                position.localPosition.set(posX.float, pos.y, pos.z)
//                val go = ctx.selectedEntityId ?: return
//                val command = TranslateCommand(go)
//                command.setBefore(pos)
//                command.setAfter(go.getLocalPosition(tempV3))
//                history.add(command)
            }
        })
        posY.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (ctx.selectedEntityId < 0) {
                    return
                }
                val position = ctx.currentWorld.getEntity(ctx.selectedEntityId)
                    .getComponent(PositionComponent::class.java)

                val pos = position.getLocalPosition(tempV3)
                position.localPosition.set(pos.x, posY.float, pos.z)
//                val go = ctx.selectedEntityId ?: return
//                val command = TranslateCommand(go)
//                val pos = go.getLocalPosition(tempV3)
//                command.setBefore(pos)
//                go.setLocalPosition(pos.x, posY.float, pos.z)
//                command.setAfter(go.getLocalPosition(tempV3))
//                history.add(command)
            }
        })
        posZ.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                if (ctx.selectedEntityId < 0) {
                    return
                }
                val position = ctx.currentWorld.getEntity(ctx.selectedEntityId)
                    .getComponent(PositionComponent::class.java)

                val pos = position.getLocalPosition(tempV3)
                position.localPosition.set(pos.x, pos.y, posZ.float)
//                val go = ctx.selectedEntityId ?: return
//                val command = TranslateCommand(go)
//                val pos = go.getLocalPosition(tempV3)
//                command.setBefore(pos)
//                go.setLocalPosition(pos.x, pos.y, posZ.float)
//                command.setAfter(go.getLocalPosition(tempV3))
//                history.add(command)
            }
        })

        // rotation
        rotX.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val go = ctx.selectedEntityId
//                val rot = go.getLocalRotation(tempQuat)
//                val rotateCommand = RotateCommand(go)
//                rotateCommand.setBefore(rot)
//                rot.setEulerAngles(rot.yaw, rotX.float, rot.roll)
//                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w)
//                rotateCommand.setAfter(go.getLocalRotation(tempQuat))
//                history.add(rotateCommand)
            }
        })
        rotY.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val go = ctx.selectedEntityId
//                val rot = go.getLocalRotation(tempQuat)
//                val rotateCommand = RotateCommand(go)
//                rotateCommand.setBefore(rot)
//                rot.setEulerAngles(rotY.float, rot.pitch, rot.roll)
//                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w)
//                rotateCommand.setAfter(go.getLocalRotation(tempQuat))
//                history.add(rotateCommand)
            }
        })
        rotZ.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val go = ctx.selectedEntityId
//                val rot = go.getLocalRotation(tempQuat)
//                val rotateCommand = RotateCommand(go)
//                rotateCommand.setBefore(rot)
//                rot.setEulerAngles(rot.yaw, rot.pitch, rotZ.float)
//                go.setLocalRotation(rot.x, rot.y, rot.z, rot.w)
//                rotateCommand.setAfter(go.getLocalRotation(tempQuat))
//                history.add(rotateCommand)
            }
        })

        // scale
        scaleX.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val go = ctx.selectedEntityId
//                if (go != null && scaleX.float > 0f) {
//                    val command = ScaleCommand(go)
//                    val scl = go.getLocalScale(tempV3)
//                    command.setBefore(scl)
//                    go.setLocalScale(scaleX.float, scl.y, scl.z)
//                    command.setAfter(go.getLocalScale(tempV3))
//                    history.add(command)
//                }
            }
        })
        scaleY.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val go = ctx.selectedEntityId
//                if (go != null && scaleY.float > 0f) {
//                    val command = ScaleCommand(go)
//                    val scl = go.getLocalScale(tempV3)
//                    command.setBefore(scl)
//                    go.setLocalScale(scl.x, scaleY.float, scl.z)
//                    command.setAfter(go.getLocalScale(tempV3))
//                    history.add(command)
//                }
            }
        })
        scaleZ.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                val go = ctx.selectedEntityId
//                if (go != null && scaleZ.float > 0f) {
//                    val command = ScaleCommand(go)
//                    val scl = go.getLocalScale(tempV3)
//                    command.setBefore(scl)
//                    go.setLocalScale(scl.x, scl.y, scaleZ.float)
//                    command.setAfter(go.getLocalScale(tempV3))
//                    history.add(command)
//                }
            }
        })

    }

    override fun setValues(entityId: Int) {
        val position = ctx.currentWorld.getEntity(entityId).getComponent(PositionComponent::class.java)

        val pos = position.localPosition
        posX.text = formatFloat(pos.x, 2)
        posY.text = formatFloat(pos.y, 2)
        posZ.text = formatFloat(pos.z, 2)

        val rot = position.localRotation
        rotX.text = formatFloat(rot.pitch, 2)
        rotY.text = formatFloat(rot.yaw, 2)
        rotZ.text = formatFloat(rot.roll, 2)

        val scl = position.localScale
        scaleX.text = formatFloat(scl.x, 2)
        scaleY.text = formatFloat(scl.y, 2)
        scaleZ.text = formatFloat(scl.z, 2)
    }

    override fun onDelete() {
        // The transform component can't be deleted.
        // Every game object has a transformation
    }

}
