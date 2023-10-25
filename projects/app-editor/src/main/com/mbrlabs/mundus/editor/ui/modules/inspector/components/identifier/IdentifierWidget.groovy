import com.mbrlabs.mundus.editor.ui.modules.inspector.components.identifier.IdentifierWidgetPresenter

return ComponentWidget {
    presenter = IdentifierWidgetPresenter.class
    showHeader = false

    content {
        row {
            Label("Name", "form")
            TextField {
                id = "name"
                layoutTypes = "form, growX"
            }
        }
        row {
            Label("Active", "form")
            CheckBox {
                id = "active"
                layoutTypes = "form"
            }
        }
        row {
            Label("Tag", "form")
            TextField {
                id = "tag"
                layoutTypes = "form, growX"
                editable = false
            }
        }
    }
}