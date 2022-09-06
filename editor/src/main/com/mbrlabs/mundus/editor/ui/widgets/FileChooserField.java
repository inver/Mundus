/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import lombok.Getter;

/**
 * @author Marcus Brummer
 * @version 30-12-2015
 */
public class FileChooserField extends VisTable {

    private int width = -1;

    @Getter
    private FileChooser.SelectionMode mode = FileChooser.SelectionMode.FILES;
    private FileSelectedListener fileSelectedListener;
    private final VisTextField textField;
    @Getter
    private final VisTextButton fcBtn;

    private FileHandle fileHandle;

    public FileChooserField(int width) {
        super();
        this.width = width;
        textField = new VisTextField();
        fcBtn = new VisTextButton("Select");

        setupUI();
    }

    public FileChooserField() {
        this(-1);
    }

    public void setEditable(boolean editable) {
        textField.setDisabled(!editable);
    }

    public String getPath() {
        return textField.getText();
    }

    public FileHandle getFile() {
        return this.fileHandle;
    }

    public void setCallback(FileSelectedListener fileSelected) {
        this.fileSelectedListener = fileSelected;
    }

    public void setFileMode(FileChooser.SelectionMode mode) {
        this.mode = mode;
    }

    public void clear() {
        textField.setText("");
        fileHandle = null;
    }

    public void setText(String text) {
        textField.setText(text);
    }

    private void setupUI() {
        if (width <= 0) {
            add(textField).expandX().fillX().padRight(5);
            add(fcBtn).expandX().fillX().row();
        } else {
            add(textField).width(width * 0.75f).padRight(5);
            add(fcBtn).expandX().fillX();
        }
    }

    public void setValue(FileHandle file) {
        textField.setText(file.path());
        if (fileSelectedListener != null) {
            fileSelectedListener.selected(file);
        }
    }

    public interface FileSelectedListener {
        void selected(FileHandle fileHandle);
    }
}
