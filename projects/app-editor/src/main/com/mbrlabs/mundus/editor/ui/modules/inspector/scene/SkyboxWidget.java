package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.FormStyle;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.editor.ui.widgets.AssetChooserField;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class SkyboxWidget extends BaseInspectorWidget {

    private final VisCheckBox enabled = new VisCheckBox("");
    private final AssetChooserField skyboxAssetField = new AssetChooserField();
    private final VisTextButton createBtn;
    private final VisTextButton editBtn;
    private final VisTextButton defaultBtn;

    public SkyboxWidget(@NotNull UiComponentHolder uiComponentHolder) {
        super(uiComponentHolder, "Skybox");

        addFormField("Enabled", enabled, false);
        addFormField("Asset", skyboxAssetField);

        createBtn = uiComponentHolder.getButtonFactory().createButton("Create new");
        editBtn = uiComponentHolder.getButtonFactory().createButton("Edit current");
        defaultBtn = uiComponentHolder.getButtonFactory().createButton("Set default");

        var style = VisUI.getSkin().get(FormStyle.FormFieldStyle.class);

        var container = new VisTable();
        container.add(createBtn).expandX().growX().padRight(style.padRight).fillX();
        container.add(editBtn).expandX().growX().padRight(style.padRight).fillX();
        container.add(defaultBtn).expandX().fillX().growX().row();
        getCollapsibleContent().add(container).expandX().padBottom(style.padBottom).colspan(2).row();
    }

    public void resetValues(boolean enabled, Asset<?> skybox) {
        this.enabled.setChecked(enabled);
        skyboxAssetField.setAsset(skybox);
    }
}
