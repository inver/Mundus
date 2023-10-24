package com.mbrlabs.mundus.editor.ui.widgets;

public interface UiField<T> {
    void setValue(T value);

    T getValue();
}
