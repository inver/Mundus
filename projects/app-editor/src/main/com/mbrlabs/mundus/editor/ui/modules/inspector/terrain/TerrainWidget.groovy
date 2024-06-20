package com.mbrlabs.mundus.editor.ui.modules.inspector.terrain

return ComponentWidget {
    title = "Terrain generation"
    presenter = TerrainWidgetPresenter.class
    showHeader = true
    layout = "top, expandX"
    debug = true

    content {
        Tabs {
            layout = "expandX, fillX, left, top"

            Tab {
                title = "Brushes"
                content {
                    row {
                        Label("Mode", "left, top")
                    }
                    row {
                        RadioButton {
                            id = "brushMode"
                            layout = "left, expandX"
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
                            layout = "expandX, fillX"
                        }
                    }
                    row {
                        Label("Strength", "left, top")
                    }
                    row {
                        Slider {
                            id = "upDownStrengthSlider"
                            layout = "expandX, fillX"
                            min = 0
                            max = 1
                            stepSize = 0.1f
                        }
                    }
                    row {
                        Table {
                            layout = "left, top"
                            content {
                                row {
                                    Label("Textures", "left, top")
                                    Button {
                                        id = "selectTexture"
                                        text = "Select"
                                        layout = "left, top"
                                    }
                                }
                            }
                        }
                    }
                    row {
                        TextureGrid {
                            id = "textureGrid"
                            layout = "left, top, growX"
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
                            layout = "form, expandX"
                            colspan = 4
                        }
                    }
                    row {
                        FileChooserField {
                            id = "heightMapChooser"
                            layout = "form, expandX"
                            colspan = 3
                        }
                        Button {
                            id = "loadHeightMapBtn"
                            text = "Load"
                            layout = "form"
                        }
                    }
                    row {
                        Label {
                            text = "Perlin noise"
                            layout = "form, expandX"
                            colspan = 4
                        }
                    }
                    row {
                        Label("Seed", "form")
                        FloatField {
                            id = "seed"
                            layout = "form, expandX"
                            colspan = 3
                        }
                    }
                    row {
                        Label("Min Height", "form")
                        FloatField {
                            id = "minHeight"
                            layout = "form, expandX"
                        }
                        Label("Max Height", "form")
                        FloatField {
                            id = "maxHeight"
                            layout = "form, expandX"
                        }
                    }
                    row {
                        Button {
                            id = "generatePerlinNoise"
                            text = "Generate"
                            layout = "form"
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
                            layout = "expandX, fillX"
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