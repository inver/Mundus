package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.editor.Mundus;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent;
import com.mbrlabs.mundus.editor.ui.UI;
import com.mbrlabs.mundus.editor.ui.widgets.TabAdapter;

public class AssetLibraryViewer {

    private final ProjectManager projectManager;
    private final GridGroup filesView = new GridGroup(80f, 4f);
    private final PopupMenu filePopupMenu = new PopupMenu();

    private final Tab tab;

    public AssetLibraryViewer(ProjectManager projectManager, String title) {
        this.projectManager = projectManager;

        var table = new VisTable(false);
        table.add(filesView).expand().fillX();
        tab = new TabAdapter(title, table);

        filesView.setTouchable(Touchable.enabled);
    }

    public void reloadAssets() {
        filesView.clearChildren();
        var projectContext = projectManager.getCurrent();
        for (var asset : projectContext.assetManager.getAssets()) {
            var assetItem = new AssetItem(asset, filePopupMenu);
            filesView.addActor(assetItem);
//            assetItems.add(assetItem)
        }
    }

    public Tab getTab() {
        return tab;
    }

    private static class AssetItem extends VisTable {
        private final Asset asset;
        private final VisLabel nameLabel;


        public AssetItem(Asset asset, PopupMenu filePopupMenu) {
            setBackground("menu-bg");

            this.asset = asset;
            nameLabel = new VisLabel(asset.toString(), "tiny");
            nameLabel.setWrap(true);

            align(Align.center);
            add(nameLabel).grow().top().row();

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (event.getButton() == Input.Buttons.RIGHT) {
                        setSelected();
                        filePopupMenu.showMenu(UI.INSTANCE, Gdx.input.getX(), (Gdx.graphics.getHeight() - Gdx.input.getY()));
                    } else if (event.getButton() == Input.Buttons.LEFT) {
                        setSelected();
                    }
                }
            });
        }

        private void setSelected() {
            //todo send event to event bus, that asses is selected
//            this @AssetsDock.setSelected(this@AssetItem)
            Mundus.INSTANCE.postEvent(new AssetSelectedEvent(asset));
//                    Mundus.postEvent(AssetSelectedEvent(asset))
        }
    }
}
