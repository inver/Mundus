package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.mbrlabs.mundus.editor.Editor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public interface EditorAppListener extends ApplicationListener {

    Editor getEditor();

    void setEditor(Editor editor);

    @Override
    default void create() {
        initVisUI();
        var ctx = new AnnotationConfigApplicationContext(RootConfig.class);
        setEditor(ctx.getBean(Editor.class));
        getEditor().create();
        var widgetsHolder = ctx.getBean(UiComponentHolder.class);
        widgetsHolder.init();
    }

    private void initVisUI() {
        var generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/robotoRegular.ttf"));
        var params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.kerning = true;
        params.borderStraight = false;
        params.genMipMaps = true;
        params.hinting = FreeTypeFontGenerator.Hinting.Full;

        // font norm
        params.size = 12;
        var fontNorm = generator.generateFont(params);

        // font small
        params.size = 11;
        var fontSmall = generator.generateFont(params);

        // font tiny
        params.size = 10;
        var fontTiny = generator.generateFont(params);
        generator.dispose();

        // skin
        var skin = new Skin();
        skin.add("font-norm", fontNorm, BitmapFont.class);
        skin.add("font-small", fontSmall, BitmapFont.class);
        skin.add("font-tiny", fontTiny, BitmapFont.class);

        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/skin/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/skin/uiskin.json"));
        VisUI.load(skin);
    }

    @Override
    default void resize(int width, int height) {
        getEditor().resize(width, height);
    }

    @Override
    default void render() {
        getEditor().render();
    }

    @Override
    default void pause() {

    }

    @Override
    default void resume() {

    }

    @Override
    default void dispose() {
        getEditor().dispose();
    }
}
