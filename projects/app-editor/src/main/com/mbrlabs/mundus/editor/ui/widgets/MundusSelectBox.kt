package com.mbrlabs.mundus.editor.ui.widgets

import com.kotcrab.vis.ui.widget.VisSelectBox

class MundusSelectBox<T> : VisSelectBox<T>() {

    private var valueRenderer: Function1<T, String>? = null

    override fun toString(item: T): String {
        if (valueRenderer != null) {
            return valueRenderer?.invoke(item)!!
        }
        return super.toString(item)
    }

    fun setValueRenderer(renderer: Function1<T, String>) {
        this.valueRenderer = renderer
    }
}