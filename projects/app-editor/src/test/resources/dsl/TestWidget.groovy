import com.mbrlabs.mundus.editor.ui.dsl.TestWidgetPresenter

final def FIELD_SIZE = 65

return ComponentWidget {
    title = "TestWidget"
    presenter = TestWidgetPresenter.class

    content {
        FloatField {
            labelText = "x"
            width = FIELD_SIZE
            layoutArgs = ""
        }

        FloatField {
            labelText = "x"
            width = FIELD_SIZE
        }
    }
}