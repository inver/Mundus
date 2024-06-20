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

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
public class TextFieldWithLabel extends VisTable {

    protected String id;
    protected VisTextField textField;
    private final VisLabel label;
    private final Cell<?> labelCell;
    private final Cell<?> textFieldCell;

    public TextFieldWithLabel(String labelText, int width) {
        super();
        label = new VisLabel(labelText);
        labelCell = add(label);
        textField = new VisTextField();
        textFieldCell = add(textField);

        setWidth(width);
    }

    public TextFieldWithLabel(String labelText) {
        this(labelText, -1);
    }

    public void setWidth(int width) {
        if (width > 0) {
            labelCell.left().width(width * 0.2f);
            textFieldCell.right().width(width * 0.8f).row();
        } else {
            labelCell.left().padRight(4f);
            textFieldCell.growX().expandX().row();
        }
    }

    public String getText() {
        return textField.getText();
    }

    public void setEditable(boolean editable) {
        textField.setDisabled(!editable);
        if (editable) {
            label.setStyle(VisUI.getSkin().get(Label.LabelStyle.class));
        } else {
            label.setStyle(VisUI.getSkin().get("disabled", Label.LabelStyle.class));
        }
    }

    public void clear() {
        textField.setText("");
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    public void setId(String id) {
        this.id = id;
    }
}
