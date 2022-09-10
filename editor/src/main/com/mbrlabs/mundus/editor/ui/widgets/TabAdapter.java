package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import lombok.Getter;

public class TabAdapter extends Tab {

    @Getter
    private final String tabTitle;
    @Getter
    private final Table contentTable;

    public TabAdapter(String title, Table contentTable) {
        this(title, contentTable, false);
    }

    public TabAdapter(String title, Table contentTable, boolean savable) {
        this(title, contentTable, savable, false);
    }

    public TabAdapter(String title, Table contentTable, boolean savable, boolean closeableByUser) {
        super(savable, closeableByUser);
        this.tabTitle = title;
        this.contentTable = contentTable;
    }
}
