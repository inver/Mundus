package com.mbrlabs.mundus.editor.ui.modules.inspector;

import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.PreviewGenerator;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;

@Slf4j
public class CameraInspector extends VisTable {

    private final PreviewGenerator previewGenerator;
    private final AppUi appUi;

    public CameraInspector(PreviewGenerator previewGenerator, AppUi appUi, ModelBatch modelBatch) {
        this.previewGenerator = previewGenerator;
        this.appUi = appUi;


//        add(previewGenerator.createPreviewWidget(appUi))

    }
}
