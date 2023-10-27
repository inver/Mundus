package com.mbrlabs.mundus.editor.ui.widgets.dsl;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import groovy.lang.Closure;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class UiTabs extends UiComponent<VisTable> {

    private final TabbedPane tabbedPane = new TabbedPane();
    private final ApplicationContext applicationContext;
    private Cell<?> contentCell;

    public UiTabs(ApplicationContext applicationContext) {
        super(new VisTable());
        this.applicationContext = applicationContext;

        actor.add(tabbedPane.getTable()).growX().fillX().padBottom(4f).row();
        contentCell = actor.add();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                contentCell.setActor(tab.getContentTable()).expand().fill();
            }
        });
    }

    public void Tab(Closure<?> closure) {
        var tab = new UiTab(applicationContext);
        closure.setDelegate(tab);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();

        tabbedPane.add(tab);
        tabbedPane.switchTab(0);
    }

    public static class UiTab extends Tab {

        private final UiFormTable table;

        @Setter
        private String title;

        public UiTab(ApplicationContext applicationContext) {
            super(false, false);
            table = new UiFormTable(applicationContext);
            table.actor.align(Align.left);
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public Table getContentTable() {
            return table.actor;
        }

        public void content(Closure<?> closure) {
            table.content(closure);
        }
    }
}
