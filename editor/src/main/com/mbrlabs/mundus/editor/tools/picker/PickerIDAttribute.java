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

package com.mbrlabs.mundus.editor.tools.picker;

import com.badlogic.gdx.graphics.g3d.Attribute;

/**
 * @author Marcus Brummer
 * @version 20-02-2016
 */
public class PickerIDAttribute extends Attribute {

    public static final String Alias = "goID";
    public static final long TYPE = register(Alias);

    public int r = 255;
    public int g = 255;
    public int b = 255;

    public static boolean is(final long mask) {
        return (mask & TYPE) == mask;
    }

    public PickerIDAttribute() {
        super(TYPE);
    }

    public PickerIDAttribute(PickerIDAttribute other) {
        super(TYPE);
    }

    @Override
    public PickerIDAttribute copy() {
        return new PickerIDAttribute(this);
    }

    @Override
    public int hashCode() {
        return r + g * 255 + b * 255 * 255;
    }

    @Override
    public int compareTo(Attribute o) {
        return -1; // TODO implement comparing
    }

    @Override
    public String toString() {
        return "GameObjectIdAttribute{" + "r=" + r + ", g=" + g + ", b=" + b + '}';
    }
}