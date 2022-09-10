package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.Asset;
import lombok.Getter;

public class AssetItem extends VisTable {
    @Getter
    private final Asset asset;

    public AssetItem(Asset asset) {
        this.asset = asset;
        setBackground("menu-bg");

//            var preview = new VisImage();
//
//            add(preview).expand().row();
        var nameLabel = new VisLabel(asset.toString(), "tiny");
        nameLabel.setWrap(true);

        align(Align.center);
        add(nameLabel).grow().top().row();
    }
}
