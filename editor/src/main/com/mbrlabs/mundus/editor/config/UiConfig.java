package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.editor.utils.Fa;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UiConfig {

    @Bean
    public BitmapFont fontAwesome() {
        var faBuilder = new Fa(Gdx.files.internal("fonts/fa45.ttf"));
//        faBuilder.getGeneratorParameter().size = Float.valueOf(Gdx.graphics.getHeight() * 0.02f).intValue();
        faBuilder.getGeneratorParameter().size = 15;
        faBuilder.getGeneratorParameter().kerning = true;
        faBuilder.getGeneratorParameter().borderStraight = false;
        //todo make bean
        return faBuilder.addIcon(Fa.Companion.getSAVE())
                .addIcon(Fa.Companion.getDOWNLOAD()).addIcon(Fa.Companion.getGIFT()).addIcon(Fa.Companion.getPLAY())
                .addIcon(Fa.Companion.getMOUSE_POINTER()).addIcon(Fa.Companion.getARROWS())
                .addIcon(Fa.Companion.getCIRCLE_O()).addIcon(Fa.Companion.getCIRCLE()).addIcon(Fa.Companion.getMINUS())
                .addIcon(Fa.Companion.getCARET_DOWN()).addIcon(Fa.Companion.getCARET_UP())
                .addIcon(Fa.Companion.getTIMES()).addIcon(Fa.Companion.getSORT()).addIcon(Fa.Companion.getHASHTAG())
                .addIcon(Fa.Companion.getPAINT_BRUSH()).addIcon(Fa.Companion.getSTAR())
                .addIcon(Fa.Companion.getREFRESH()).addIcon(Fa.Companion.getEXPAND())
                .addIcon(Fa.Companion.getPLUS_SQUARE()).addIcon(Fa.Companion.getMINUS_SQUARE())
                .addIcon(Fa.Companion.getSQUARE())
                .build();
    }

    @Bean
    public FileChooser fileChooser() {
        FileChooser.setDefaultPrefsName("com.mbrlabs.mundus.editor");
        return new FileChooser(FileChooser.Mode.OPEN);
    }
}
