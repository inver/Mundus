package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.editor.ui.widgets.TabAdapter;
import lombok.Getter;

public class AssetLibraryViewer {

    @Getter
    private final GridGroup filesView = new GridGroup(80f, 4f);
    @Getter
    private final PopupMenu filePopupMenu = new PopupMenu();
    private final Tab tab;

    public AssetLibraryViewer(String title) {
        var table = new VisTable(false);
        table.add(filesView).expand().fillX();
        tab = new TabAdapter(title, table);

        filesView.setTouchable(Touchable.enabled);
    }

    public Tab getTab() {
        return tab;
    }

    public void setSelected(AssetItem assetItem) {
        for (var child : filesView.getChildren()) {
            var item = (AssetItem) child;
            if (assetItem != null && assetItem == item) {
                item.background(VisUI.getSkin().getDrawable("default-select-selection"));
            } else {
                item.background(VisUI.getSkin().getDrawable("menu-bg"));
            }
        }
    }

    public static class AssetItem extends VisTable {

        public AssetItem(Asset asset) {
            setBackground("menu-bg");

            var nameLabel = new VisLabel(asset.toString(), "tiny");
            nameLabel.setWrap(true);

            align(Align.center);
            add(nameLabel).grow().top().row();
        }
    }
}
