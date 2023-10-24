package com.mbrlabs.mundus.editor.ui.modules.inspector.components.transform

final def FIELD_SIZE = 15

return ComponentWidget {
    title = "Transformation"
    presenter = TransformWidgetPresenter.class

    content {
        row {
            Label("Position:", "form")
            Label("x", "form")
            FloatField {
                id = "posX"
                layoutTypes = "form"
            }
            Label("y", "form")
            FloatField {
                id = "posY"
                layoutTypes = "form"
            }
            Label("z", "form")
            FloatField {
                id = "posZ"
                layoutTypes = "form"
            }
        }
        row {
            Label("Rotation:", "form")
            Label("x", "form")
            FloatField {
                id = "rotX"
                layoutTypes = "form"
            }
            Label("y", "form")
            FloatField {
                id = "rotY"
                layoutTypes = "form"
            }
            Label("z", "form")
            FloatField {
                id = "rotZ"
                layoutTypes = "form"
            }
        }
        row {
            Label("Scale:", "form")
            Label("x", "form")
            FloatField {
                id = "sclX"
                layoutTypes = "form"
            }
            Label("y", "form")
            FloatField {
                id = "sclY"
                layoutTypes = "form"
            }
            Label("z", "form")
            FloatField {
                id = "sclZ"
                layoutTypes = "form"
            }
        }
    }
}