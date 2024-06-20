import com.mbrlabs.mundus.editor.ui.modules.inspector.scene.AmbientLightPresenter
import com.mbrlabs.mundus.editor.ui.modules.inspector.scene.FogPresenter
import com.mbrlabs.mundus.editor.ui.modules.inspector.scene.SkyboxPresenter

return Table {
    content {
        row {
            ComponentWidget {
                title = "Ambient light"
                presenter = AmbientLightPresenter.class
                layout = "expandX, fillX, left, top"
                showHeader = true

                content {
                    row {
                        Label {
                            text = "Enabled"
                            layout = "form"
                        }
                        CheckBox {
                            id = "enabled"
                            layout = "form"
                        }
                    }
                    row {
                        Label {
                            text = "Intensity"
                            layout = "form"
                        }
                        FloatField {
                            id = "intensity"
                            layout = "form, growX"
                        }
                    }
                    row {
                        Label {
                            text = "Color"
                            layout = "form"
                        }
                        ColorChooserField {
                            id = "color"
                            layout = "form, growX"
                        }
                    }
                }
            }
        }
        row {
            ComponentWidget {
                title = "Fog"
                presenter = FogPresenter.class
                layout = "expandX, fillX, left, top"

                content {
                    row {
                        Label("Enabled", "form")
                        CheckBox {
                            id = "enabled"
                            layout = "form"
                        }
                    }
                    row {
                        Label("Density", "form")
                        FloatField {
                            id = "density"
                            layout = "form, growX"
                        }
                    }
                    row {
                        Label("Gradient", "form")
                        FloatField {
                            id = "gradient"
                            layout = "form, growX"
                        }
                    }
                    row {
                        Label("Color", "form")
                        ColorChooserField {
                            id = "color"
                            layout = "form, growX"
                        }
                    }
                }
            }
        }
        row {
            ComponentWidget {
                title = "Skybox"
                presenter = SkyboxPresenter.class
                layout = "expandX, fillX, left, top"

                content {
                    row {
                        Label("Enabled", "form")
                        CheckBox {
                            id = "enabled"
                            layout = "form"
                        }
                    }
                    row {
                        Label("Asset", "form")
                        AssetChooserField {
                            id = "asset"
                            layout = "form, growX"
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