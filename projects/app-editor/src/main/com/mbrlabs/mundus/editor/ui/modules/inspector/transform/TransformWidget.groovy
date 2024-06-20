package com.mbrlabs.mundus.editor.ui.modules.inspector.transform

final def FIELD_SIZE = 15

return ComponentWidget {
    title = "Transformation"
    presenter = TransformWidgetPresenter.class
    layout = "top, expandX"

    content {
        row {
            Label("Position:", "form")
            Label("x", "form")
            FloatField {
                id = "posX"
                layout = "form"
                width = 45f
            }
            Label("y", "form")
            FloatField {
                id = "posY"
                layout = "form"
                width = 45f
            }
            Label("z", "form")
            FloatField {
                id = "posZ"
                layout = "form"
                width = 45f
            }
        }
        row {
            Label("Rotation:", "form")
            Label("x", "form")
            FloatField {
                id = "rotX"
                layout = "form"
                width = 45f
            }
            Label("y", "form")
            FloatField {
                id = "rotY"
                layout = "form"
                width = 45f
            }
            Label("z", "form")
            FloatField {
                id = "rotZ"
                layout = "form"
                width = 45f
            }
        }
        row {
            Label("Scale:", "form")
            Label("x", "form")
            FloatField {
                id = "sclX"
                layout = "form"
                width = 45f
            }
            Label("y", "form")
            FloatField {
                id = "sclY"
                layout = "form"
                width = 45f
            }
            Label("z", "form")
            FloatField {
                id = "sclZ"
                layout = "form"
                width = 45f
            }
        }
    }
}