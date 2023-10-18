package com.mbrlabs.mundus.editor.ui.modules.dialogs.terrain;

import com.mbrlabs.mundus.editor.core.assets.EditorTerrainService;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.SceneGraphChangedEvent;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.ui.modules.outline.ClickButtonListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerrainDialogPresenter {

    private final EditorCtx ctx;
    private final EditorTerrainService editorTerrainService;
    private final EventBus eventBus;
    private final ProjectManager projectManager;
    private final ToolManager toolManager;

    public void initGenerateButton(AddTerrainDialog dialog) {
        dialog.generateBtn.addListener(new ClickButtonListener(() -> {
            try {
                String name = dialog.name.getText();
                int resolution = Integer.parseInt(dialog.vertexResolution.getText());
                int width = Integer.parseInt(dialog.terrainWidth.getText());
                float posX = Float.parseFloat(dialog.positionX.getText());
                float posY = Float.parseFloat(dialog.positionY.getText());
                float posZ = Float.parseFloat(dialog.positionZ.getText());


                var entityId = editorTerrainService.createTerrain(name, resolution, width, posX, posY, posZ);

                projectManager.saveProject(ctx.getCurrent());
                //todo is needed import event here?
//                eventBus.post(new AssetImportEvent(asset));

                selectEntityAndFireEvents(entityId);
            } catch (NumberFormatException nfe) {
                log.error(nfe.getMessage(), nfe);
            }
        }));
    }

    private void selectEntityAndFireEvents(int createdId) {
        toolManager.translateTool.entitySelected(createdId);
        eventBus.post(new SceneGraphChangedEvent());
        eventBus.post(new EntitySelectedEvent(createdId));
    }
}
