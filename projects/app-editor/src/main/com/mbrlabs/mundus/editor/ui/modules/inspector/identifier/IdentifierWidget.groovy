package com.mbrlabs.mundus.editor.ui.modules.inspector.identifier

return ComponentWidget {
    presenter = IdentifierWidgetPresenter.class
    showHeader = false

    content {
        row {
            Label("Name", "form")
            TextField {
                id = "name"
                layout = "form, growX"
            }
        }
        row {
            Label("Active", "form")
            CheckBox {
                id = "active"
                layout = "form"
            }
        }
        row {
            Label("Tag", "form")
            TextField {
                id = "tag"
                layout = "form, growX"
                editable = false
            }
        }
    }
}