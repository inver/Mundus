package com.mbrlabs.mundus.editor.config;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import com.mbrlabs.mundus.editor.Editor;
import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.widgets.icon.FontRenderer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class InitListener extends Lwjgl3WindowAdapter implements ApplicationListener {

    private Editor editor;

    @Override
    public void create() {
        initVisUI();
        var ctx = new AnnotationConfigApplicationContext(RootConfig.class);
        editor = ctx.getBean(Editor.class);
        editor.create();
        var widgetsHolder = ctx.getBean(UiComponentHolder.class);
        widgetsHolder.init();
    }

    //todo move all font preparing to FontGenerator
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

        var symbolFontRenderer = new FontRenderer(Gdx.files.internal("fonts/materialSymbolsRounded.ttf"));

        // skin
        var skin = new Skin();
        skin.add("font-norm", fontNorm, BitmapFont.class);
        skin.add("font-small", fontSmall, BitmapFont.class);
        skin.add("font-tiny", fontTiny, BitmapFont.class);
        skin.add("font-symbol-regular", symbolFontRenderer.getRegular(), BitmapFont.class);
        skin.add("font-symbol-small", symbolFontRenderer.getSmall(), BitmapFont.class);
        skin.add("font-symbol-tiny", symbolFontRenderer.getTiny(), BitmapFont.class);

        skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/skin/uiskin.atlas")));
        skin.load(Gdx.files.internal("ui/skin/uiskin.json"));
        VisUI.load(skin);
    }

    @Override
    public void resize(int width, int height) {
        editor.resize(width, height);
    }

    @Override
    public void render() {
        editor.render();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        editor.dispose();
    }

    @Override
    public boolean closeRequested() {
        return editor.closeRequested();
    }
}
