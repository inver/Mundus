package com.mbrlabs.mundus.editor.ui.widgets.dsl;

public interface UiField<T> {
    void setValue(T value);

    T getValue();
}
