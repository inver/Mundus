import com.mbrlabs.mundus.editor.ui.modules.inspector.scene.AmbientLightPresenter
import com.mbrlabs.mundus.editor.ui.modules.inspector.scene.FogPresenter
import com.mbrlabs.mundus.editor.ui.modules.inspector.scene.SkyboxPresenter

return Table {
    content {
        row {
            ComponentWidget {
                title = "Ambient light"
                presenter = AmbientLightPresenter.class
                layoutTypes = "expandX, fillX, left, top"
                showHeader = true

                content {
                    row {
                        Label {
                            text = "Enabled"
                            layoutTypes = "form"
                        }
                        CheckBox {
                            id = "enabled"
                            layoutTypes = "form"
                        }
                    }
                    row {
                        Label {
                            text = "Intensity"
                            layoutTypes = "form"
                        }
                        FloatField {
                            id = "intensity"
                            layoutTypes = "form, growX"
                        }
                    }
                    row {
                        Label {
                            text = "Color"
                            layoutTypes = "form"
                        }
                        ColorChooserField {
                            id = "color"
                            layoutTypes = "form, growX"
                        }
                    }
                }
            }
        }
        row {
            ComponentWidget {
                title = "Fog"
                presenter = FogPresenter.class
                layoutTypes = "expandX, fillX, left, top"

                content {
                    row {
                        Label("Enabled", "form")
                        CheckBox {
                            id = "enabled"
                            layoutTypes = "form"
                        }
                    }
                    row {
                        Label("Density", "form")
                        FloatField {
                            id = "density"
                            layoutTypes = "form, growX"
                        }
                    }
                    row {
                        Label("Gradient", "form")
                        FloatField {
                            id = "gradient"
                            layoutTypes = "form, growX"
                        }
                    }
                    row {
                        Label("Color", "form")
                        ColorChooserField {
                            id = "color"
                            layoutTypes = "form, growX"
                        }
                    }
                }
            }
        }
        row {
            ComponentWidget {
                title = "Skybox"
                presenter = SkyboxPresenter.class
                layoutTypes = "expandX, fillX, left, top"

                content {
                    row {
                        Label("Enabled", "form")
                        CheckBox {
                            id = "enabled"
                            layoutTypes = "form"
                        }
                    }
                    row {
                        Label("Asset", "form")
                        AssetChooserField {
                            id = "asset"
                            layoutTypes = "form, growX"
                        }
                    }
                    row {
                        Table {
                            colspan = 3
                            content {
                                Button {
                                    id = "createNew"
                                    text = "Create new"
                                }
                                Button {
                                    id = "editCurrent"
                                    text = "Edit Current"
                                }
                                Button {
                                    id = "setDefault"
                                    text = "Set Default"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}