package com.mbrlabs.mundus.editor.ui.widgets.presenter;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog;
import com.mbrlabs.mundus.editor.ui.widgets.AssetSelectionField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetChooserFieldPresenter {
    private final AssetPickerDialog assetPickerDialog;

    public void init(AssetSelectionField field) {
        field.getSelectButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assetPickerDialog.show(true, field.getAssetFilter(), asset -> {
                    field.setAsset(asset);
                    if (field.getPickerListener() != null) {
                        field.getPickerListener().onSelected(asset);
                    }
                });
            }
        });
    }
}
