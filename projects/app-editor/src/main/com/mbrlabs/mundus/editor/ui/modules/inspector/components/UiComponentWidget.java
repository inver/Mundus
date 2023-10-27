package com.mbrlabs.mundus.editor.ui.modules.inspector.components;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.outline.ClickButtonListener;
import com.mbrlabs.mundus.editor.ui.widgets.CollapseWidget;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiComponent;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiFormTable;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

public class UiComponentWidget extends UiComponent<VisTable> {

    @Getter
    @Setter
    private Class<UiComponentPresenter<UiComponentWidget>> presenter;

    private final VisLabel titleLabel = new VisLabel();
    private final VisTable header = new VisTable();

    protected final UiComponentHolder uiComponentHolder;

    private final VisTextButton collapseBtn;
    private final VisTextButton deleteBtn;
    private Cell<VisTable> headerCell;
    private Cell<VisTextButton> deletableBtnCell;

    @Getter
    private final UiFormTable contentTable;
    private final CollapseWidget collapsibleWidget;
    private Cell<CollapseWidget> collapseWidgetCell;

    @Getter
    private boolean deletable = false;
    private boolean showHeader = true;

    @Setter
    @Getter
    protected int entityId = -1;

    public UiComponentWidget(ApplicationContext applicationContext) {
        super(new VisTable());
        uiComponentHolder = applicationContext.getBean(UiComponentHolder.class);
        collapseBtn = uiComponentHolder.getButtonFactory().createButton(SymbolIcon.EXPAND_MORE);
        deleteBtn = uiComponentHolder.getButtonFactory().createButton(SymbolIcon.CLOSE);

        contentTable = new UiFormTable(applicationContext);
        collapsibleWidget = new CollapseWidget(contentTable.getActor());

        deleteBtn.getStyle().up = null;

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        // header
        deletableBtnCell = header.add(deleteBtn).top().left();
        header.add(titleLabel);
        header.add(collapseBtn).right().top().width(20f).height(20f).expand().row();
        header.add(new Separator(uiComponentHolder.getSeparatorStyle())).fillX().expandX().colspan(3).row();

        // add everything to root
        headerCell = actor.add(header);
        headerCell.expand().fill().row();

        var style = VisUI.getSkin().get(BaseInspectorWidget.BaseWidgetInspectorStyle.class);
        contentTable.getActor().padTop(style.getPadTop());
        collapseWidgetCell = actor.add(collapsibleWidget);
        collapseWidgetCell.expand().fill().row();

        setDeletable(false);
    }

    /**
     * Method used from UI dsl to set title of widget
     *
     * @param title title to set
     */
    @SuppressWarnings("unused")
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
        if (deletable) {
            deleteBtn.setVisible(true);
            deletableBtnCell.width(20f).height(20f).padRight(8f);
        } else {
            deleteBtn.setVisible(false);
            deletableBtnCell.width(0).height(0).padRight(0);
        }
    }

    public boolean isCollapsed() {
        return collapsibleWidget.isCollapsed();
    }

    public void collapse(boolean collapse) {
        collapsibleWidget.setCollapsed(collapse, false);
        if (collapse) {
            collapseBtn.setText(SymbolIcon.EXPAND_LESS.getSymbol());
        } else {
            collapseBtn.setText(SymbolIcon.EXPAND_MORE.getSymbol());
        }
    }

    private void setupListeners() {
        collapseBtn.addListener(new ClickButtonListener(() -> collapse(!isCollapsed())));
        deleteBtn.addListener(new ClickButtonListener(this::onDelete));
    }

    public void setVisible(boolean visible) {
        if (visible) {
            setShowHeader(showHeader);
            collapsibleWidget.setCollapsed(false, false);
            collapseWidgetCell.padBottom(8f);
        } else {
            showHeader = header.isVisible();
            setShowHeader(false);
            collapsibleWidget.setCollapsed(true, false);
            collapseWidgetCell.padBottom(0f);
        }
    }

    public void content(Closure<?> closure) {
        contentTable.content(closure);
    }

    public void onDelete() {
        //do nothing
    }

    /**
     * Method used from UI dsl for show or hide header
     *
     * @param value if true -> show header, otherwise hide
     */
    @SuppressWarnings("unused")
    public void setShowHeader(boolean value) {
        header.setVisible(value);
        if (value) {
            headerCell.height(20f);
        } else {
            headerCell.height(0f);
        }
    }

    public void setDebug(boolean value) {
        if (value) {
            contentTable.getActor().debugAll();
        }
    }

}
