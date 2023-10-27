package com.mbrlabs.mundus.editor.ui.widgets.dsl.grid;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiComponent;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;

public class UiButtonGrid extends UiComponent<GridGroup> {
    private static final TextButton.TextButtonStyle STYLE = VisUI.getSkin().get("bg", TextButton.TextButtonStyle.class);

    public UiButtonGrid() {
        super(new GridGroup());
    }

    public VisTextButton addIconButton(SymbolIcon icon) {
        var res = new VisTextButton(icon.getSymbol());
        res.setStyle(STYLE);
        res.setFocusBorderEnabled(false);
        actor.addActor(res);
        return res;
    }

    public void setItemSize(float size) {
        actor.setItemSize(size);
    }

    public void setSpacing(float spacing) {
        actor.setSpacing(spacing);
    }
}
