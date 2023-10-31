package com.mbrlabs.mundus.editor.ui.modules.inspector.material;

import com.badlogic.gdx.graphics.g3d.Material;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.assets.AssetType;
import com.mbrlabs.mundus.commons.assets.material.MaterialAsset;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.widgets.chooser.asset.AssetChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.chooser.color.ColorChooserField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialWidgetPresenter implements UiComponentPresenter<UiComponentWidget> {
    private final EditorCtx ctx;
    private final EventBus eventBus;

    @Override
    public void init(UiComponentWidget uiComponent) {
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            if (ctx.isEntitySelected()) {
                uiComponent.setVisible(false);
            }
        });
        eventBus.register((AssetSelectedEvent.AssetSelectedListener) event -> {
            if (event.getAsset().getType() != AssetType.MATERIAL) {
                uiComponent.setVisible(false);
                return;
            } else {
                uiComponent.setVisible(true);
            }
            var asset = (MaterialAsset) event.getAsset();

            //todo
//            uiComponent.getField("previewImage", VisImage.class).setDrawable(asset.getTexture());
            uiComponent.getField("nameLabel", VisLabel.class).setText(asset.getName());
            uiComponent.getField("diffuseColor", ColorChooserField.class).setSelectedColor(asset.getDiffuseColor());
//            uiComponent.getField("diffuseTexture", AssetChooserField.class)
//            .setSelectedColor(asset.getDiffuseTexture());
        });
    }

    public void setMaterial(UiComponentWidget matComponent, String name, Material material) {
        matComponent.setVisible(true);
        matComponent.getField("nameLabel", VisLabel.class).setText(name);
    }
}
