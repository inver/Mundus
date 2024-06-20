package com.mbrlabs.mundus.editor.ui.modules.inspector.terrain.tabs.generation

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.history.Command
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel
import com.mbrlabs.mundus.editor.ui.widgets.IntegerFieldWithLabel

class PerlinNoiseTab : Tab(false, false) {
    private val root = VisTable()

    val perlinNoiseBtn = VisTextButton("Generate Perlin noise")
    val perlinNoiseSeed = IntegerFieldWithLabel("Seed", -1, false)
    val perlinNoiseMinHeight = FloatFieldWithLabel("Min height", -1, true)
    val perlinNoiseMaxHeight = FloatFieldWithLabel("Max height", -1, true)

    init {
        root.align(Align.left)
        root.add(perlinNoiseSeed).pad(5f).left().fillX().expandX().row()
        root.add(perlinNoiseMinHeight).pad(5f).left().fillX().expandX().row()
        root.add(perlinNoiseMaxHeight).pad(5f).left().fillX().expandX().row()
        root.add(perlinNoiseBtn).pad(5f).right().row()
    }

    override fun getTabTitle(): String = "Perlin noise"

    override fun getContentTable(): Table = root

    //todo move to presenter
    fun generatePerlinNoise(seed: Int, min: Float, max: Float): Command? {
//        val terrain = terrainAsset.terrain
//        val command = TerrainHeightCommand(terrain)
//        command.setHeightDataBefore(terrain.heightData)
//
//        Terraformer.perlin(terrain).minHeight(min).maxHeight(max).seed(seed.toLong()).terraform()
//
//        command.setHeightDataAfter(terrain.heightData)
        return null
    }
}