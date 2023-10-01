package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.badlogic.gdx.graphics.Color;
import com.mbrlabs.mundus.commons.env.lights.BaseLight;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SceneInspectorPresenter {
    private final EditorCtx ctx;

    public void setIntensity(Float value) {
        var environment = ctx.getCurrent().getCurrentScene().getEnvironment();
        var oldColor = environment.getAmbientLight().getColor();
        environment.setAmbientLight(oldColor, value);
    }

    public void setColor(Color newColor) {
        var environment = ctx.getCurrent().getCurrentScene().getEnvironment();
        var oldIntensity = environment.getAmbientLight().getIntensity();
        environment.setAmbientLight(newColor, oldIntensity);
    }

    public BaseLight getValue() {
        return ctx.getCurrent().getCurrentScene().getEnvironment().getAmbientLight();
    }
}
