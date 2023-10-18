package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SceneInspector extends VisTable {


    public SceneInspector(UiComponentHolder uiComponentHolder, SceneInspectorPresenter sceneInspectorPresenter) {

        var ambientLightWidget = new AmbientLightWidget(uiComponentHolder);
        sceneInspectorPresenter.initAmbientLightWidget(ambientLightWidget);
        add(ambientLightWidget).expandX().fillX().top().left().row();

        var fogWidget = new FogWidget(uiComponentHolder);
        sceneInspectorPresenter.initFogWidget(fogWidget);
        add(fogWidget).expandX().fillX().top().left().row();

        var skyboxWidget = new SkyboxWidget(uiComponentHolder);
        sceneInspectorPresenter.initSkyboxWidget(skyboxWidget);
        add(skyboxWidget).expandX().fillX().top().left().row();

        add(new VisTable()).grow().fill();
    }
}
