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

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.kotcrab.vis.ui.widget.VisRadioButton;
import com.kotcrab.vis.ui.widget.VisTable;
import lombok.Getter;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
@Getter
public class RadioButtonGroup extends VisTable {

    /**
     * A checkbox with a reference object.
     */
    @Getter
    public static class RadioButton extends VisRadioButton {

        private final Object refObject;

        public RadioButton(String text, Object refObject) {
            super(text);
            this.refObject = refObject;
        }

    }

    private final ButtonGroup<RadioButton> buttonGroup;

    public RadioButtonGroup() {
        super();
        buttonGroup = new ButtonGroup<>();
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        pad(5);
    }

    public void addOption(String text, Object refObject) {
        var button = new RadioButton(text, refObject);
        buttonGroup.add(button);
        add(button).left().top();
    }
}
