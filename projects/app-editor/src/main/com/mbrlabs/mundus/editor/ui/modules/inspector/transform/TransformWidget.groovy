package com.mbrlabs.mundus.editor.ui.modules.inspector.transform

final def FIELD_SIZE = 15

return ComponentWidget {
    title = "Transformation"
    presenter = TransformWidgetPresenter.class
    layoutStyle = "top, expandX"

    content {
        row {
            Label("Position:", "form")
            Label("x", "form")
            FloatField {
                id = "posX"
                layoutTypes = "form"
                width = 45f
            }
            Label("y", "form")
            FloatField {
                id = "posY"
                layoutTypes = "form"
                width = 45f
            }
            Label("z", "form")
            FloatField {
                id = "posZ"
                layoutTypes = "form"
                width = 45f
            }
        }
        row {
            Label("Rotation:", "form")
            Label("x", "form")
            FloatField {
                id = "rotX"
                layoutTypes = "form"
                width = 45f
            }
            Label("y", "form")
            FloatField {
                id = "rotY"
                layoutTypes = "form"
                width = 45f
            }
            Label("z", "form")
            FloatField {
                id = "rotZ"
                layoutTypes = "form"
                width = 45f
            }
        }
        row {
            Label("Scale:", "form")
            Label("x", "form")
            FloatField {
                id = "sclX"
                layoutTypes = "form"
                width = 45f
            }
            Label("y", "form")
            FloatField {
                id = "sclY"
                layoutTypes = "form"
                width = 45f
            }
            Label("z", "form")
            FloatField {
                id = "sclZ"
                layoutTypes = "form"
                width = 45f
            }
        }
    }
}