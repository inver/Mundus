package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.widgets.FloatField;
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorChooserField;
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorChooserPresenter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FogPresenter implements UiComponentPresenter<UiComponentWidget> {
    private final EditorCtx ctx;
    private final EventBus eventBus;
    private final ColorChooserPresenter colorChooserPresenter;

    @Override
    public void init(UiComponentWidget uiComponent) {
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> fillFromEnvironment(uiComponent));
        eventBus.register((SceneChangedEvent.SceneChangedListener) event -> fillFromEnvironment(uiComponent));
        uiComponent.getField("density", FloatField.class).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var value = convert(((VisTextField) actor).getText());
                if (value != null) {
                    var env = getEnv();
                    env.setFog(env.getFog().getColor(), value, env.getFog().getGradient());
                }
            }
        });
        uiComponent.getField("gradient", FloatField.class).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var value = convert(((VisTextField) actor).getText());
                if (value != null) {
                    var env = getEnv();
                    env.setFog(env.getFog().getColor(), env.getFog().getDensity(), value);
                }
            }
        });
        colorChooserPresenter.init(uiComponent.getField("color", ColorChooserField.class));
        uiComponent.getField("color", ColorChooserField.class).setColorAdapter(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                var env = getEnv();
                env.setFog(newColor, env.getFog().getDensity(), env.getFog().getGradient());
            }
        });
        uiComponent.getField("enabled", VisCheckBox.class).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((VisCheckBox) actor).isChecked()) {
                    getEnv().enableFog();
                } else {
                    getEnv().disableFog();
                }
            }
        });
    }

    private void fillFromEnvironment(UiComponentWidget uiComponent) {
        var environment = getEnv();
        uiComponent.getField("enabled", VisCheckBox.class).setChecked(environment.isFogEnabled());
        uiComponent.getField("color", ColorChooserField.class).setSelectedColor(environment.getFog().getColor());
        uiComponent.getField("density", FloatField.class).setValue(environment.getFog().getDensity());
        uiComponent.getField("gradient", FloatField.class).setValue(environment.getFog().getGradient());
    }

    private SceneEnvironment getEnv() {
        return ctx.getCurrent().getCurrentScene().getEnvironment();
    }

    @Nullable
    private Float convert(String input) {
        try {
            if (input.isEmpty()) {
                return null;
            }
            return Float.valueOf(input);
        } catch (Exception e) {
            return null;
        }
    }
}
