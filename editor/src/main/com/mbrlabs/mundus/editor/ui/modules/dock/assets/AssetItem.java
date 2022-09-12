package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import com.mbrlabs.mundus.editor.ui.modules.IconUtils;
import com.mbrlabs.mundus.editor.utils.TextureUtils;
import lombok.Getter;

public class AssetItem extends WidgetGroup {
    @Getter
    private final Asset asset;

    public AssetItem(Asset asset, PreviewGenerator generator) {
        debugAll();
        this.asset = asset;
//        setBackground("menu-bg");

        if (asset.getType() == AssetType.TEXTURE) {
            addActor(generator.generate(asset));
//            add(preview).expand().row();
        } else if (asset.getType() == AssetType.MATERIAL) {
            addActor(generator.generate(asset));

//            addActor(widget);//.width(80f).height(80f).expand().row();
        } else {

        }

        addActor(new VisImage(TextureUtils.load(IconUtils.getIcon(asset).getPath(), 20, 20)));

        var nameLabel = new VisLabel(asset.getName(), "tiny");
        nameLabel.setWrap(true);

//        align(Align.center);
        addActor(nameLabel);//.grow().top().row();
    }
}
