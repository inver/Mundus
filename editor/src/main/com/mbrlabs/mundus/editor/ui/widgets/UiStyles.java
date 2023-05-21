package com.mbrlabs.mundus.editor.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.kotcrab.vis.ui.VisUI;
import com.mbrlabs.mundus.editor.ui.widgets.icon.FontRenderer;
import org.springframework.stereotype.Component;

@Component
public class UiStyles {
    private static final Color TEAL_COLOR = new Color(0x00b695ff);

    private final FontRenderer fontRenderer;

    private final TextButton.TextButtonStyle styleNoBg = new TextButton.TextButtonStyle();
    private final TextButton.TextButtonStyle styleBg = new TextButton.TextButtonStyle();
    private final TextButton.TextButtonStyle styleActive = new TextButton.TextButtonStyle();

    public UiStyles(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;

        initStyle(styleNoBg, TEAL_COLOR);
        initStyle(styleActive, Color.WHITE);
        initStyle(styleBg, TEAL_COLOR);
        styleBg.up = VisUI.getSkin().getDrawable("menu-bg");
        styleBg.down = VisUI.getSkin().getDrawable("button-over");
        styleBg.over = VisUI.getSkin().getDrawable("button-over");
    }

    private void initStyle(TextButton.TextButtonStyle style, Color color) {
        style.font = fontRenderer.getFont();
        style.pressedOffsetX = 1;
        style.unpressedOffsetX = 0;
        style.pressedOffsetY = -1;
        style.fontColor = color;
    }

    public TextButton.TextButtonStyle getStyleNoBg() {
        return styleNoBg;
    }

    public TextButton.TextButtonStyle getStyleBg() {
        return styleBg;
    }

    public TextButton.TextButtonStyle getStyleActive() {
        return styleActive;
    }
}
