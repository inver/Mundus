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
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.ComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.UiComponentWidget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FogPresenter implements UiComponentPresenter<UiComponentWidget> {
    private final EditorCtx ctx;
    private final EventBus eventBus;

    @Override
    public void init(UiComponentWidget uiComponent) {
//        eventBus.register((ProjectChangedEvent.ProjectChangedListener) event -> {
//            var environment = getEnv();
//            fogWidget.resetValues(environment.isFogEnabled(), environment.getFog());
//        });
//        eventBus.register((SceneChangedEvent.SceneChangedListener) event -> {
//            var environment = getEnv();
//            fogWidget.resetValues(environment.isFogEnabled(), environment.getFog());
//        });
//        fogWidget.getDensity().addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                var value = convert(((VisTextField) actor).getText());
//                if (value != null) {
//                    var env = getEnv();
//                    env.setFog(env.getFog().getColor(), value, env.getFog().getGradient());
//                }
//            }
//        });
//        fogWidget.getGradient().addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                var value = convert(((VisTextField) actor).getText());
//                if (value != null) {
//                    var env = getEnv();
//                    env.setFog(env.getFog().getColor(), env.getFog().getDensity(), value);
//                }
//            }
//        });
//        colorPickerPresenter.init(fogWidget.getColorPickerField());
//        fogWidget.getColorPickerField().setColorAdapter(new ColorPickerAdapter() {
//            @Override
//            public void finished(Color newColor) {
//                var env = getEnv();
//                env.setFog(newColor, env.getFog().getDensity(), env.getFog().getGradient());
//            }
//        });
//        fogWidget.getEnabled().addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (((VisCheckBox) actor).isChecked()) {
//                    getEnv().enableFog();
//                } else {
//                    getEnv().disableFog();
//                }
//            }
//        });
    }

    private SceneEnvironment getEnv() {
        return ctx.getCurrent().getCurrentScene().getEnvironment();
    }

}
