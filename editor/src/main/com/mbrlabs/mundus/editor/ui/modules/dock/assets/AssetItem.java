package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import com.mbrlabs.mundus.editor.ui.modules.IconUtils;
import com.mbrlabs.mundus.editor.utils.TextureUtils;
import lombok.Getter;

import static com.badlogic.gdx.utils.Align.bottom;
import static com.mbrlabs.mundus.editor.ui.UiConstants.ASSET_BAR_PREVIEW_SIZE;

public class AssetItem extends WidgetGroup {
    @Getter
    private final Asset<?> asset;

    public AssetItem(Asset<?> asset, PreviewGenerator generator) {
        this.asset = asset;
//        setBackground("menu-bg");

        if (asset.getType() == AssetType.TEXTURE) {
            addActor(generator.generate(asset));
        } else if (asset.getType() == AssetType.MATERIAL) {
            addActor(generator.generate(asset));
        } else {

        }

        var table = new VisTable();
//        table.debugAll();
        table.setWidth(ASSET_BAR_PREVIEW_SIZE);
        table.setHeight(20f);

        var bgPixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0, 0, 0, 0.6f));
        bgPixmap.fill();

        table.setBackground(new TextureRegionDrawable(new Texture(bgPixmap)));
        table.setPosition(ASSET_BAR_PREVIEW_SIZE / 2, 0, bottom);

        table.add(new VisImage(TextureUtils.load(IconUtils.getIcon(asset).getPath(), 20, 20)))
                .width(20).height(20).padRight(2);

        var nameLabel = new VisLabel(asset.getName(), "tiny");
        nameLabel.setWrap(true);
        table.add(nameLabel).growX().row();

        addActor(table);
    }
}
