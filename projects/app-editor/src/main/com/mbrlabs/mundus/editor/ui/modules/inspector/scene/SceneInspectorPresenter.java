package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.SceneChangedEvent;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SceneInspectorPresenter {
    private final EditorCtx ctx;
    private final EventBus eventBus;

    public BaseLight getValue() {
        return getEnv().getAmbientLight();
    }

    public void initAmbientLightWidget(AmbientLightWidget ambientLightWidget) {
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            var environment = getEnv();
            ambientLightWidget.resetValues(environment.isAmbientLightEnabled(), environment.getAmbientLight());
        });
        eventBus.register((SceneChangedEvent.SceneChangedListener) event -> {
            var environment = getEnv();
            ambientLightWidget.resetValues(environment.isAmbientLightEnabled(), environment.getAmbientLight());
        });
        ambientLightWidget.getIntensity().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var value = convert(((VisTextField) actor).getText());
                if (value != null) {
                    var environment = getEnv();
                    var oldColor = environment.getAmbientLight().getColor();
                    environment.setAmbientLight(oldColor, value);
                }
            }
        });
        ambientLightWidget.getColorPickerField().setColorAdapter(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                var environment = getEnv();
                var oldIntensity = environment.getAmbientLight().getIntensity();
                environment.setAmbientLight(newColor, oldIntensity);
            }
        });
        ambientLightWidget.getEnabled().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((VisCheckBox) actor).isChecked()) {
                    getEnv().enableAmbientLight();
                } else {
                    getEnv().disableAmbientLight();
                }
            }
        });
    }

    public void initFogWidget(FogWidget fogWidget) {
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
            var environment = getEnv();
            fogWidget.resetValues(environment.isFogEnabled(), environment.getFog());
        });
        eventBus.register((SceneChangedEvent.SceneChangedListener) event -> {
            var environment = getEnv();
            fogWidget.resetValues(environment.isFogEnabled(), environment.getFog());
        });
        fogWidget.getDensity().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var value = convert(((VisTextField) actor).getText());
                if (value != null) {
                    var env = getEnv();
                    env.setFog(env.getFog().getColor(), value, env.getFog().getGradient());
                }
            }
        });
        fogWidget.getGradient().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var value = convert(((VisTextField) actor).getText());
                if (value != null) {
                    var env = getEnv();
                    env.setFog(env.getFog().getColor(), env.getFog().getDensity(), value);
                }
            }
        });
        fogWidget.getColorPickerField().setColorAdapter(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                var env = getEnv();
                env.setFog(newColor, env.getFog().getDensity(), env.getFog().getGradient());
            }
        });
        fogWidget.getEnabled().addListener(new ChangeListener() {
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
