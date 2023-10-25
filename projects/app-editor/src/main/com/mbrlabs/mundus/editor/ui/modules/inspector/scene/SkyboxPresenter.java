package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.assets.AssetManager;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.skybox.SkyboxDialog;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.outline.ClickButtonListener;
import com.mbrlabs.mundus.editor.ui.widgets.AssetChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorChooserPresenter;
import com.mbrlabs.mundus.editor.ui.widgets.presenter.AssetChooserFieldPresenter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkyboxPresenter implements UiComponentPresenter<UiComponentWidget> {

    private final AppUi appUi;
    private final EditorCtx ctx;
    private final EventBus eventBus;
    private final AssetChooserFieldPresenter assetChooserFieldPresenter;
    private final AssetManager assetManager;
    private final SkyboxDialog skyboxDialog;

    @Override
    public void init(UiComponentWidget uiComponent) {
        assetChooserFieldPresenter.init(uiComponent.getField("asset", AssetChooserField.class));
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event ->
                onProjectOrSceneChanged(uiComponent));
        eventBus.register((SceneChangedEvent.SceneChangedListener) event -> onProjectOrSceneChanged(uiComponent));
        uiComponent.getField("enabled", VisCheckBox.class).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getEnv().setSkyboxEnabled(((VisCheckBox) actor).isChecked());
            }
        });
        uiComponent.getField("asset", AssetChooserField.class)
                .setPickerListener(asset -> setSkyboxName(asset.getName()));
        uiComponent.getField("setDefault", VisTextButton.class)
                .addListener(new ClickButtonListener(() -> setSkyboxName(ProjectConstants.DEFAULT_SKYBOX_NAME)));

        uiComponent.getField("createNew", VisTextButton.class).addListener(new ClickButtonListener(() -> {
            skyboxDialog.init(null);
            appUi.showDialog(skyboxDialog);
        }));
        uiComponent.getField("setDefault", VisTextButton.class).addListener(new ClickButtonListener(() -> {
            skyboxDialog.init(getEnv().getSkyboxName());
            appUi.showDialog(skyboxDialog);
        }));
    }

    private void onProjectOrSceneChanged(UiComponentWidget uiComponent) {
        var environment = getEnv();
        Asset<?> asset = null;
        if (StringUtils.isNotEmpty(environment.getSkyboxName())) {
            asset = assetManager.loadCurrentProjectAsset(environment.getSkyboxName());
        }
        uiComponent.getField("enabled", VisCheckBox.class).setChecked(environment.isSkyboxEnabled());
        uiComponent.getField("asset", AssetChooserField.class).setAsset(asset);
    }

    private void setSkyboxName(String name) {
        ctx.getCurrent().getCurrentScene().getEnvironment().setSkyboxName(name);

        eventBus.post(new SceneChangedEvent());
    }

    private SceneEnvironment getEnv() {
        return ctx.getCurrent().getCurrentScene().getEnvironment();
    }

}
