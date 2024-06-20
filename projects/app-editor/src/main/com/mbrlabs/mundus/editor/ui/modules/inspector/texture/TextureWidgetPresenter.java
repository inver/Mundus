package com.mbrlabs.mundus.editor.ui.modules.inspector.texture;

import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.outline.IdNode;
import com.mbrlabs.mundus.editor.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextureWidgetPresenter implements UiComponentPresenter<UiComponentWidget> {

    private final EventBus eventBus;

    @Override
    public void init(UiComponentWidget uiComponent) {
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            if (event.getEntityId() > IdNode.ROOT_NODE_ASSET) {
                uiComponent.setVisible(false);
            }
        });
        eventBus.register((AssetSelectedEvent.AssetSelectedListener) event -> {
            if (event.getAsset().getType() != AssetType.TEXTURE) {
                uiComponent.setVisible(false);
                return;
            } else {
                uiComponent.setVisible(true);
            }

            var asset = (TextureAsset) event.getAsset();
            uiComponent.getField("previewImage", VisImage.class).setDrawable(asset.getTexture());
            uiComponent.getField("sizeLabel", VisLabel.class)
                    .setText(StringUtils.formatFloat(asset.getSize(), 2) + " Mb");
            uiComponent.getField("nameLabel", VisLabel.class).setText(asset.getName());
            uiComponent.getField("widthLabel", VisLabel.class).setText(asset.getTexture().getWidth() + " px");
            uiComponent.getField("heightLabel", VisLabel.class).setText(asset.getTexture().getHeight() + " px");
        });
    }
}
