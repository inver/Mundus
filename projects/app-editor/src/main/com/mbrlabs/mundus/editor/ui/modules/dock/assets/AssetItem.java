package com.mbrlabs.mundus.editor.ui.modules.dock.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import com.mbrlabs.mundus.editor.ui.IconUtils;
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
        } else if (asset.getType() == AssetType.MODEL) {
//            addActor(generator.generate(asset));
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

        var icon = new VisLabel(
                IconUtils.getIcon(asset).getSymbol(), VisUI.getSkin().get("icon", Label.LabelStyle.class)
        );
        icon.setFontScale(0.7f);
        table.add(icon).width(20).height(20).padRight(2).padLeft(4).padTop(2);

        var nameLabel = new VisLabel(asset.getName(), "tiny");
        nameLabel.setWrap(true);
        table.add(nameLabel).growX().row();

        addActor(table);
    }
}
