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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Modified version of Kotcrab's CollapsibleWidget.
 * https://github.com/kotcrab/VisEditor/blob/b2ea63a045e805099ddd16778517d46d2419a637/UI/src/com/kotcrab/vis/ui/widget/CollapsibleWidget.java
 * <p>
 * Size of expandable content expands to parent WidgetGroup.
 * <p>
 * In layout():
 * <p>
 * table.setBounds(0, 0, table.getPrefWidth(), table.getPrefHeight()); ===>
 * table.setBounds(0, 0, getWidth(), getHeight());
 */
public class CollapseWidget extends WidgetGroup {
    private Table table;

    private final CollapseAction collapseAction = new CollapseAction();

    private boolean collapsed;
    private boolean actionRunning;

    private float currentHeight;

    public CollapseWidget(Table table) {
        this(table, false);
    }

    public CollapseWidget(Table table, boolean collapsed) {
        this.collapsed = collapsed;
        this.table = table;

        updateTouchable();

        if (table != null) {
            addActor(table);
        }
    }

    public void setCollapsed(boolean collapse, boolean withAnimation) {
        this.collapsed = collapse;
        updateTouchable();

        if (table == null) {
            return;
        }

        actionRunning = true;

        if (withAnimation) {
            addAction(collapseAction);
        } else {
            if (collapse) {
                currentHeight = 0;
                collapsed = true;
            } else {
                currentHeight = table.getPrefHeight();
                collapsed = false;
            }

            actionRunning = false;
            invalidateHierarchy();
        }
    }

    public void setCollapsed(boolean collapse) {
        setCollapsed(collapse, true);
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    private void updateTouchable() {
        if (collapsed) {
            setTouchable(Touchable.disabled);
        } else {
            setTouchable(Touchable.enabled);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (currentHeight > 1) {
            batch.flush();
            boolean clipEnabled = clipBegin(getX(), getY(), getWidth(), currentHeight);

            super.draw(batch, parentAlpha);

            batch.flush();
            if (clipEnabled) {
                clipEnd();
            }
        }
    }

    @Override
    public void layout() {
        if (table == null) {
            return;
        }

        table.setBounds(0, 0, getWidth(), getHeight());

        if (!actionRunning) {
            if (collapsed) {
                currentHeight = 0;
            } else {
                currentHeight = table.getPrefHeight();
            }
        }
    }

    @Override
    public float getPrefWidth() {
        return table == null ? 0 : table.getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        if (table == null) {
            return 0;
        }

        if (!actionRunning) {
            if (collapsed) {
                return 0;
            } else {
                return table.getPrefHeight();
            }
        }

        return currentHeight;
    }

    public void setTable(Table table) {
        this.table = table;
        clearChildren();
        addActor(table);
    }

    @Override
    protected void childrenChanged() {
        super.childrenChanged();
        if (getChildren().size > 1) {
            throw new GdxRuntimeException("Only one actor can be added to CollapsibleWidget");
        }
    }

    private class CollapseAction extends Action {
        @Override
        public boolean act(float delta) {
            if (collapsed) {
                currentHeight -= delta * 1000;
                if (currentHeight <= 0) {
                    currentHeight = 0;
                    collapsed = true;
                    actionRunning = false;
                }
            } else {
                currentHeight += delta * 1000;
                if (currentHeight > table.getPrefHeight()) {
                    currentHeight = table.getPrefHeight();
                    collapsed = false;
                    actionRunning = false;
                }
            }

            invalidateHierarchy();
            return !actionRunning;
        }
    }

}
