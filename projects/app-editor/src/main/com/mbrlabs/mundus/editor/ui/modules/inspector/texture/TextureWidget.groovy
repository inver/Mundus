import com.mbrlabs.mundus.editor.ui.modules.inspector.texture.TextureWidgetPresenter

return ComponentWidget {
    title = "Texture"
    presenter = TextureWidgetPresenter.class
    showHeader = true
    layoutStyle = "top, expandX"

    content {
        row {
            Image {
                id = "previewImage"
                height = 200f
                width = 200f
                colspan = 4
            }
        }
        row {
            Label("Name", "form")
            Label {
                id = "nameLabel"
                layoutStyle = "form, expandX"
            }
            Label("Width", "form")
            Label {
                id = "widthLabel"
                layoutStyle = "form, expandX"
            }
        }
        row {
            Label("Size", "form")
            Label {
                id = "sizeLabel"
                layoutStyle = "form, expandX"
            }
            Label("Height", "form")
            Label {
                id = "heightLabel"
                layoutStyle = "form, expandX"
            }
        }
    }
}