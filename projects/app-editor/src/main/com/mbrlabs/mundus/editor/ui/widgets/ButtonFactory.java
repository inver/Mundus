package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ButtonFactory {

    public VisTextButton createButton(String text, TextButton.TextButtonStyle style) {
        var res = new VisTextButton(text);
        res.setStyle(style);
        res.setFocusBorderEnabled(false);
        return res;
    }

    public VisTextButton createButton(SymbolIcon icon) {
        return createButton(icon.getSymbol(), VisUI.getSkin().get("bg", TextButton.TextButtonStyle.class));
    }

    public VisTextButton createButton(String text) {
        return createButton(text, VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class));
    }

//    public
}
