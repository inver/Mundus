package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.editor.ui.widgets.icon.FontRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UiConfig {

    @Bean
    public FontRenderer fontRenderer() {
        return new FontRenderer(Gdx.files.internal("fonts/materialSymbolsRounded.ttf"));
    }

    @Bean
    public FileChooser fileChooser() {
        FileChooser.setDefaultPrefsName("com.mbrlabs.mundus.editor");
        return new FileChooser(FileChooser.Mode.OPEN);
    }
}
