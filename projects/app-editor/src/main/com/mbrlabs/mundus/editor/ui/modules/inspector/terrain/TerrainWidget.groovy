package com.mbrlabs.mundus.editor.ui.modules.inspector.terrain

return ComponentWidget {
    title = "Terrain generation"
    presenter = TerrainWidgetPresenter.class
    showHeader = true
    layoutStyle = "top, expandX"

    content {
        Tabs {
            layoutTypes = "expandX, fillX, left, top"

            Tab {
                title = "Brushes"
                content {
                    row {
                        Label("Mode", "left, top")
                    }
                    row {
                            RadioButton {
                            id = "brushMode"
                            layoutTypes = "left, expandX"
                        }
                    }
                    row {
                        Label("Shape", "left, top")
                    }
                    row {
                        ButtonGrid {
                            id = "brushButtonGrid"
                            itemSize = 30f
                            spacing = 0f
                            layoutTypes = "expandX, fillX"
                        }
                    }
                    row {
                        Label("Strength", "left, top")
                    }
                    row {
                        Slider {
                            id = "upDownStrengthSlider"
                            layoutTypes = "expandX, fillX"
                            min = 0
                            max = 1
                            stepSize = 0.1f
                        }
                    }
                    row {
                        Table{
                            layoutTypes = "left, top"
                            content{
                                row{
                                    Label("Textures", "left, top")
                                    Button {
                                        id = "selectTexture"
                                        text = "Select"
                                        layoutTypes = "left, top"
                                    }
                                }
                            }
                        }
                    }
                    row {
                        TextureGrid {
                            id = "textureGrid"
                            layoutTypes = "left, top, growX"
                            itemSize = 40f
                            spacing = 1f
                        }
                    }
                }
            }
            Tab {
                title = "Generation"
                content {
                    row {
                        Label {
                            text = "Height map"
                            layoutTypes = "form, expandX"
                            colspan = 4
                        }
                    }
                    row {
                        FileChooserField {
                            id = "heightMapChooser"
                            layoutTypes = "form, expandX"
                            colspan = 3
                        }
                        Button {
                            id = "loadHeightMapBtn"
                            text = "Load"
                            layoutTypes = "form"
                        }
                    }
                    row {
                        Label {
                            text = "Perlin noise"
                            layoutTypes = "form, expandX"
                            colspan = 4
                        }
                    }
                    row {
                        Label("Seed", "form")
                        FloatField {
                            id = "seed"
                            layoutTypes = "form, expandX"
                            colspan = 3
                        }
                    }
                    row {
                        Label("Min Height", "form")
                        FloatField {
                            id = "minHeight"
                            layoutTypes = "form, expandX"
                        }
                        Label("Max Height", "form")
                        FloatField {
                            id = "maxHeight"
                            layoutTypes = "form, expandX"
                        }
                    }
                    row {
                        Button {
                            id = "generatePerlinNoise"
                            text = "Generate"
                            layoutTypes = "form"
                        }
                    }
                }
            }
            Tab {
                title = "Settings"
                content {
                    row { Label("UV Scaling", "left, top") }
                    row {
                        Slider {
                            id = "settingsUVScaling"
                            layoutTypes = "expandX, fillX"
                            min = 0.5f
                            max = 120
                            stepSize = 0.5f
                        }
                    }
                }
            }
        }
    }
}