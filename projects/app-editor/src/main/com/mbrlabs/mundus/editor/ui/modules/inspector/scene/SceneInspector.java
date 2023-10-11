package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.editor.config.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.AppUi;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SceneInspector extends VisTable {
    private final SceneInspectorPresenter sceneInspectorPresenter;
    private final UiComponentHolder uiComponentHolder;
    private final AppUi appUi;
    private final FileChooser fileChooser;


    public SceneInspector(UiComponentHolder uiComponentHolder, SceneInspectorPresenter sceneInspectorPresenter,
                          AppUi appUi, FileChooser fileChooser) {
        this.sceneInspectorPresenter = sceneInspectorPresenter;
        this.uiComponentHolder = uiComponentHolder;
        this.appUi = appUi;
        this.fileChooser = fileChooser;

        var ambientLightWidget = new AmbientLightWidget(uiComponentHolder);
        sceneInspectorPresenter.initAmbientLightWidget(ambientLightWidget);
        add(ambientLightWidget).expandX().fillX().top().left().row();

        var fogWidget = new FogWidget(uiComponentHolder);
        sceneInspectorPresenter.initFogWidget(fogWidget);
        add(fogWidget).expandX().fillX().top().left().row();


        setupUI();
//        setupListeners();
    }

    private void setupUI() {
        add(new SkyboxWidget(appUi, uiComponentHolder, fileChooser)).expandX().fillX().top().left().row();
        add(new VisTable()).grow().fill();
    }


    private void resetValues() {
//        var ambientLight = sceneInspectorPresenter.getValue();
//        intensity.setText(ambientLight.getIntensity() + "");
//        colorPickerField.setColor(ambientLight.getColor());
    }

    private Float convert(String input) {
        try {
            if (input.isEmpty()) {
                return null;
            }
            return java.lang.Float.valueOf(input);
        } catch (Exception e) {
            return null;
        }

    }
//
//    override fun
//
//    onProjectChanged(event:ProjectChangedEvent) {
//        resetValues()
//    }
//
//    override fun
//
//    onSceneChanged(event:SceneChangedEvent) {
//        resetValues()
//    }
}
