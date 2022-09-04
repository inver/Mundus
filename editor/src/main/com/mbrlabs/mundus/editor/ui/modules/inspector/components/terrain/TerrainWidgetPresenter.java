package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.editor.ui.AppUi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerrainWidgetPresenter {

    private final AppUi appUi;
    private final ToolManager toolManager;

    public void initBrushGrid(TerrainBrushGrid grid) {
        for (var brush : toolManager.terrainBrushes) {
            var actor = grid.addBrush(brush);
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    grid.activateBrush(brush);
                    try {
                        toolManager.activateTool(brush);
                    } catch (TerrainBrush.ModeNotSupportedException e) {
                        log.error("ERROR", e);
                        Dialogs.showErrorDialog(appUi, e.getMessage());
                    }

                }
            });
        }
    }
}
