package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.mbrlabs.mundus.editor.ui.widgets.colorPicker.ColorPickerField;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SceneInspector extends VisTable {
    private final SceneInspectorPresenter sceneInspectorPresenter;

    private final VisTextField intensity = new VisTextField("0");
    private final ColorPickerField colorPickerField = new ColorPickerField();

    public SceneInspector(SceneInspectorPresenter sceneInspectorPresenter) {
        this.sceneInspectorPresenter = sceneInspectorPresenter;
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        var root = new Table();
        root.padTop(6f).padRight(6f).padBottom(22f);
        add(root);

        root.add(new VisLabel("Intensity: ")).left().padBottom(10f);
        root.add(intensity).fillX().expandX().padBottom(10f).row();
        root.add(new VisLabel("Color")).growX().row();
        root.add(colorPickerField).left().fillX().expandX().colspan(2).row();
    }

    private void setupListeners() {
        intensity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var value = convert(intensity.getText());
                if (value != null) {
                    sceneInspectorPresenter.setIntensity(value);
                }
            }
        });
        colorPickerField.setColorAdapter(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                sceneInspectorPresenter.setColor(newColor);
            }
        });
    }

    private void resetValues() {
        var ambientLight = sceneInspectorPresenter.getValue();
        intensity.setText(ambientLight.getIntensity() + "");
        colorPickerField.setColor(ambientLight.getColor());
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
