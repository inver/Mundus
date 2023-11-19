package com.mbrlabs.mundus.editor.ui.modules.inspector.material

return ComponentWidget {
    title = "Material"
    presenter = MaterialWidgetPresenter.class
    showHeader = true
    styleTypes = "top, expandX"

    content {
        row {
            Image {
                id = "previewImage"
                height = 200f
                width = 200f
                colpan = 2
            }
        }
        row {
            Label("Name", "form, fillX")
            Label {
                id = "nameLabel"
                styleTypes = "form, growX"
            }
        }
        row {
            Label("Diffuse Color", "form")
            ColorChooserField {
                id = "diffuseColor"
                styleTypes = "form, growX"
            }
        }
        row {
            Label("Diffuse Texture", "form")
            AssetChooserField {
                id = "diffuseTexture"
                styleTypes = "form, growX"
            }
        }
    }
}