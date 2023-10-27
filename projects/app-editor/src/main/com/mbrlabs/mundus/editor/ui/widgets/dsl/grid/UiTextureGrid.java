package com.mbrlabs.mundus.editor.ui.widgets.dsl.grid;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.utils.TextureProvider;
import com.mbrlabs.mundus.editor.ui.widgets.dsl.UiComponent;

import java.util.List;

public class UiTextureGrid extends UiComponent<GridGroup> {

    private OnTextureClickedListener listener;

    public UiTextureGrid() {
        super(new GridGroup());
    }

    public void setListener(OnTextureClickedListener listener) {
        this.listener = listener;
    }

    public void setTextures(List<TextureProvider> textures) {
        actor.clearChildren();
        for (var tex : textures) {
            addTexture(tex);
        }
    }

    public void addTexture(TextureProvider texture) {
        actor.addActor(new Item(texture));
    }

    public void removeTextures() {
        actor.clearChildren();
    }

    public interface OnTextureClickedListener {
        void onTextureSelected(TextureProvider textureProvider, boolean leftClick);
    }

    public void setItemSize(float size) {
        actor.setItemSize(size);
    }

    public void setSpacing(float spacing) {
        actor.setSpacing(spacing);
    }

    private class Item extends VisTable {
        public Item(TextureProvider provider) {
            add(new VisImage(provider.getTexture()));

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (listener != null) {
                        listener.onTextureSelected(provider, button == Input.Buttons.LEFT);
                    }
                }
            });

        }
    }
}
